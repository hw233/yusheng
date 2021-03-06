/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */

package l1j.server.server.model.Instance;

/*import java.io.PrintStream;*/
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import l1j.server.server.datatables.EnchantDmgReductionTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.PetTable;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PCAction;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_ItemName;
import l1j.server.server.serverpackets.S_Light;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.templates.L1Armor;
import l1j.server.server.templates.L1CharaterTrade;
import l1j.server.server.templates.L1EnchantDmgreduction;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.templates.L1Pet;
import l1j.server.server.utils.BinaryOutputStream;
import l1j.william.L1WilliamWeaponSkill;
import l1j.william.WeaponSkill;

// Referenced classes of package l1j.server.server.model:
// L1Object, L1PcInstance

public class L1ItemInstance extends L1Object {

	private static final long serialVersionUID = 1L;

	private long _count;

	private int _itemId;

	private L1Item _item;

	private boolean _isEquipped = false;

	private int _enchantLevel;

	private boolean _isIdentified = false;

	private int _durability;

	private int _chargeCount;

	private Timestamp _lastUsed = null;

	private int _lastWeight;

	private int _attrEnchantKind;

	private int _attrEnchantLevel;

	private int _attrEnchantCount;

	private int _gamNo;// ??????????????????
	private int _gamNpcId;// ????????????NPC??????
	private String _gamNpcName;// ??????NPC??????

	private Timestamp _overSeal = null;

	private final LastStatus _lastStatus = new LastStatus();

	// ?????????
	private LightTimer _lightTimer;
	private L1ItemInstance _Item;

	class LightTimer extends TimerTask {

		public LightTimer() {
		}

		public void run() {
			try {
				if (getChargeCount() > 0 && getEnchantLevel() != 0
						&& _Item != null) { // ??????????????? 0
					if ((getChargeCount() - 4) > 0) {
						setChargeCount(getChargeCount() - 4); // ??????4?????????

						_lightTimer = null;

						// ????????????
						_lightTimer = new LightTimer();
						(new Timer()).schedule(_lightTimer, 4000);
						// ????????????
					} else { // ?????????
						setChargeCount(0);

						_lightTimer = null;

						if (_owner != null
								&& _owner.getInventory().checkItem(
										getItem().getItemId())) {
							_owner.setPcLight(0);
							Light(_owner, _Item); // ??????

							if (getItemId() == 40005) {
								_owner.getInventory().removeItem(_Item, 1); // ????????????????????????
							}
						}
					}

					if (_owner != null
							&& _owner.getInventory().checkItem(
									getItem().getItemId())) {
						_owner.getInventory().updateItem(_Item,
								L1PcInventory.COL_CHARGE_COUNT);
					}
				}
			} catch (Exception e) {
			}
		}
	}

	// ????????? end

	// ???????????????????????????
	private L1PcInstance _owner;
	private boolean _isRunning = false;
	private EnchantTimer _timer;

	class EnchantTimer extends TimerTask {

		public EnchantTimer() {
		}

		public void run() {
			try {
				int type = getItem().getType2();
				int item_id = getItem().getItemId();
				setEnchant(0);
				if (_owner != null && _owner.getInventory().checkItem(item_id)) {
					if (type == 2 && isEquipped()) { // ???????????????
						_owner.getEquipSlot().OnChanceAc();
						_owner.sendPackets(new S_OwnCharStatus(_owner));
					}
					_owner.sendPackets(new S_ServerMessage(308, getLogName()));
				}
				setHolyEnchant(0);
				setHit(0);
				_isRunning = false;
				_timer = null;
			} catch (Exception e) {
			}
		}
	}

	public void setEnchantSkill(final L1PcInstance pc) {
		if (_isRunning) {
			_owner = pc;
		}
	}

	public boolean isRunning() {
		return _isRunning;
	}

	// ??????????????????????????? end

	public L1ItemInstance() {
		_count = 1;
		_enchantLevel = 0;
		_bless = 1;
	}

	public L1ItemInstance(L1Item item, long count) {
		this();
		setItem(item);
		setCount(count);
		setBless(item.getBless());
	}

	/**
	 * ?????????????????????(??????)??????????????????????????????
	 * 
	 * @return ??????????????????true??????????????????false???
	 */
	public boolean isIdentified() {
		return _isIdentified;
	}

	/**
	 * ?????????????????????(??????)????????????????????????????????????
	 * 
	 * @param identified
	 *            ??????????????????true??????????????????false???
	 */
	public void setIdentified(boolean identified) {
		_isIdentified = identified;
	}

	public String getName() {
		return _item.getName();
	}

	/**
	 * ?????????????????????????????????
	 * 
	 * @return ?????????????????????
	 */
	public long getCount() {
		return _count;
	}

	/**
	 * ???????????????????????????????????????
	 * 
	 * @param l
	 *            ?????????????????????
	 */
	public void setCount(long l) {
		_count = l;
	}

	/**
	 * ???????????????????????????????????????????????????
	 * 
	 * @return ???????????????????????????????????????true?????????????????????????????????false???
	 */
	public boolean isEquipped() {
		return _isEquipped;
	}

	/**
	 * ?????????????????????????????????????????????????????????
	 * 
	 * @param equipped
	 *            ???????????????????????????????????????true,??????????????????????????????false???
	 */
	public void setEquipped(boolean equipped) {
		_isEquipped = equipped;
	}

	public L1Item getItem() {
		return _item;
	}

	public void setItem(L1Item item) {
		_item = item;
		_itemId = item.getItemId();
		_bless = item.getBless();
	}

	public int getItemId() {
		return _itemId;
	}

	public void setItemId(int itemId) {
		_itemId = itemId;
	}

	public boolean isStackable() {
		return _item.isStackable();
	}

	@Override
	public void onAction(L1PcInstance player) {
	}

	public int getEnchantLevel() {
		return _enchantLevel;
	}

	public void setEnchantLevel(int enchantLevel) {
		_enchantLevel = enchantLevel;
	}

	public int get_gfxid() {
		return _item.getGfxId();
	}

	public int get_durability() {
		return _durability;
	}

	public int getChargeCount() {
		return _chargeCount;
	}

	public void setChargeCount(int i) {
		_chargeCount = i;
	}

	public void setLastUsed(Timestamp t) {
		_lastUsed = t;
	}

	public Timestamp getLastUsed() {
		return _lastUsed;
	}

	public int getLastWeight() {
		return _lastWeight;
	}

	public void setLastWeight(int weight) {
		_lastWeight = weight;
	}

	public void setAttrEnchantKind(int i) {
		_attrEnchantKind = i;
	}

	public int getAttrEnchantKind() {
		return _attrEnchantKind;
	}

	public void setAttrEnchantLevel(int i) {
		_attrEnchantLevel = i;
	}

	public int getAttrEnchantLevel() {
		return _attrEnchantLevel;
	}

	public void setAttrEnchantCount(int i) {
		_attrEnchantCount = i;
	}

	public int getAttrEnchantCount() {
		return _attrEnchantCount;
	}

	public int getMr() {
		int mr = _item.get_mdef();
		if (getItemId() == 20011 || getItemId() == 20110
				|| getItemId() == 120011) {
			mr += getEnchantLevel();
		}
		if (getItemId() == 20056 || getItemId() == 120056
				|| getItemId() == 220056) {
			mr += getEnchantLevel() * 2;
		}
		return mr;
	}

	/*
	 * ????????????0~127?????? -???????????????????????????
	 */
	public void set_durability(int i) {
		if (i < 0) {
			i = 0;
		}

		if (i > 127) {
			i = 127;
		}
		_durability = i;
	}

	public int getWeight() {
		if (getItem().getWeight() == 0) {
			return 0;
		} else {
			return (int) Math.max(getCount() * getItem().getWeight() / 1000, 1);
		}
	}

	/**
	 * ??????DB???????????????????????????????????????????????????????????????????????????
	 */
	public class LastStatus {
		public long count;

		public int itemId;

		public boolean isEquipped = false;

		public int enchantLevel;

		public boolean isIdentified = true;

		public int durability;

		public int chargeCount;

		public Timestamp lastUsed = null;

		public int attrEnchantKind;

		public int attrEnchantLevel;

		public int gamNo;// ??????????????????

		public int gamNpcId;// ????????????NPC??????

		public String gamNpcName;// ????????????NPC??????

		public Timestamp overSeal = null;

		public boolean isseal = false;

		public boolean isTradable;

		public boolean _isOndurability;

		public int bless;

		public int _updatePVP;

		public int _updatePVE;

		public void updateAll() {
			count = getCount();
			itemId = getItemId();
			isEquipped = isEquipped();
			isIdentified = isIdentified();
			enchantLevel = getEnchantLevel();
			durability = get_durability();
			chargeCount = getChargeCount();
			lastUsed = getLastUsed();
			attrEnchantKind = getAttrEnchantKind();
			attrEnchantLevel = getAttrEnchantLevel();
			gamNo = getGamNo();
			gamNpcId = getGamNpcId();
			gamNpcName = getGamNpcName();
			isseal = isSeal();
			overSeal = getOverSeal();
			bless = getBless();
			isTradable = isTradable();
			_isOndurability = isOndurability();
			_updatePVP = getUpdatePVP();
			_updatePVE = getUpdatePVE();
		}

		public void updateCount() {
			count = getCount();
		}

		public void updateItemId() {
			itemId = getItemId();
		}

		public void updateEquipped() {
			isEquipped = isEquipped();
		}

		public void updateIdentified() {
			isIdentified = isIdentified();
		}

		public void updateEnchantLevel() {
			enchantLevel = getEnchantLevel();
		}

		public void updateDuraility() {
			durability = get_durability();
		}

		public void updateChargeCount() {
			chargeCount = getChargeCount();
		}

		public void updateAttrEnchantKind() {
			attrEnchantKind = getAttrEnchantKind();
		}

		public void updateAttrEnchantLevel() {
			attrEnchantLevel = getAttrEnchantLevel();
		}

		public void updateGamNo() {
			gamNo = getGamNo();
		}

		public void updateGamNpcId() {
			gamNpcId = getGamNpcId();
		}

		public void updateGamNpcName() {
			gamNpcName = getGamNpcName();
		}

		public void updateSeal() {
			isseal = isSeal();
		}

		public void updateOverSeal() {
			overSeal = getOverSeal();
		}

		public void updateLastUsed() {
			lastUsed = getLastUsed();
		}

		public void updateBless() {
			bless = getBless();
		}

		public void updateTradable() {
			isTradable = isTradable();
		}

		public void updateOndurability() {
			_isOndurability = isOndurability();
		}

		// ??????PVP
		public void updatePVP() {
			_updatePVP = getUpdatePVP();
		}

		// ??????PVE
		public void updatePVE() {
			_updatePVE = getUpdatePVE();
		}
	}

	public LastStatus getLastStatus() {
		return _lastStatus;
	}

	/**
	 * ??????DB???????????????????????????????????????????????????????????????????????????????????????
	 */
	public int getRecordingColumns() {
		int column = 0;

		if (getCount() != _lastStatus.count) {
			column += L1PcInventory.COL_COUNT;
		}
		if (getItemId() != _lastStatus.itemId) {
			column += L1PcInventory.COL_ITEMID;
		}
		if (isEquipped() != _lastStatus.isEquipped) {
			column += L1PcInventory.COL_EQUIPPED;
		}
		if (getEnchantLevel() != _lastStatus.enchantLevel) {
			column += L1PcInventory.COL_ENCHANTLVL;
		}
		if (get_durability() != _lastStatus.durability) {
			column += L1PcInventory.COL_DURABILITY;
		}
		if (getChargeCount() != _lastStatus.chargeCount) {
			column += L1PcInventory.COL_CHARGE_COUNT;
		}
		if (getLastUsed() != _lastStatus.lastUsed) {
			column += L1PcInventory.COL_DELAY_EFFECT;
		}
		if (isIdentified() != _lastStatus.isIdentified) {
			column += L1PcInventory.COL_IS_ID;
		}
		if (getAttrEnchantKind() != _lastStatus.attrEnchantKind) {
			column += L1PcInventory.COL_ATTR_ENCHANT_KIND;
		}
		if (getAttrEnchantLevel() != _lastStatus.attrEnchantLevel) {
			column += L1PcInventory.COL_ATTR_ENCHANT_LEVEL;
		}
		if (isSeal() != _lastStatus.isseal) {
			column += L1PcInventory.COL_SEAL;
		}
		if (getOverSeal() != _lastStatus.overSeal) {
			column += L1PcInventory.COL_OVERSEAL;
		}
		if (getBless() != _lastStatus.bless) {
			column += L1PcInventory.COL_BLESS;
		}
		if (isTradable() != _lastStatus.isTradable) {
			column += L1PcInventory.ITEM_TRADABLE;
		}
		if (isOndurability() != _lastStatus._isOndurability) {
			column += L1PcInventory.COL_Ondurabilit;
		}
		return column;
	}

	/**
	 * ????????????????????????????????????????????????????????????????????????????????????<br>
	 */
	public String getNumberedViewName(long _count2) {
		StringBuilder name = new StringBuilder(getNumberedName(_count2));
		if (get_time() != null) {
			final SimpleDateFormat sdf = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm");
			name.append("[" + sdf.format(get_time()) + "]"); // ????????????
		}
		int itemType2 = getItem().getType2();

		if (getItem().getItemId() == 40314 || // ??????????????????????????????
				getItem().getItemId() == 40316) {
			L1Pet pet = PetTable.getInstance().getTemplate(getId());
			if (pet != null) {
				L1Npc npc = NpcTable.getInstance().getTemplate(pet.get_npcid());
				name.append("[Lv." + pet.get_level() + " " + npc.get_nameid()
						+ "]");
			}
		}

		// ????????????????????????
		if (isIdentified() && getItem().getItemId() == 20383) {
			name.append(" [" + getChargeCount() + "]");
		}
		// ???????????????????????? end
		/*
		 * if (isIdentified() && getItem().getItemId() == 20288) {
		 * name.append(" [" + getChargeCount() + "]"); }
		 */

		// ????????????
		if ((getItem().getItemId() == 40001 || getItem().getItemId() == 40002
				|| getItem().getItemId() == 40004 || getItem().getItemId() == 40005)
				&& getEnchantLevel() != 0) {
			name.append(" ($10)");
		}
		if ((getItem().getItemId() == 40001 || getItem().getItemId() == 40002)
				&& getChargeCount() <= 0) {
			name.append(" ($11)");
		}
		// ???????????? end

		if (isEquipped()) {
			if (itemType2 == 1) {
				name.append(" ($9)"); // ??????(Armed)
			} else if (itemType2 == 2) {
				name.append(" ($117)"); // ??????(Worn)
			}
		}

		if (isOndurability()) {
			name.append("[$15188]");
		}

		if (this.getItemCharaterTrade() != null) {
			name.append("[");
			name.append(this.getItemCharaterTrade().getName());
			name.append(" Lv");
			name.append(this.getItemCharaterTrade().getLevel());
			name.append(" ");
			name.append(L1PCAction.TYPE_CLASS[this.getItemCharaterTrade()
					.get_Type()]);
			name.append("]");
		}

		return name.toString();
	}

	/**
	 * LOG???
	 */
	public String getNumberedLogViewName(long _count2) {
		StringBuilder name = new StringBuilder(getViewNumberedName(_count2));
		// int itemType2 = getItem().getType2();

		if (getItem().getItemId() == 40314 || // ??????????????????????????????
				getItem().getItemId() == 40316) {
			L1Pet pet = PetTable.getInstance().getTemplate(getId());
			if (pet != null) {
				L1Npc npc = NpcTable.getInstance().getTemplate(pet.get_npcid());
				name.append("[Lv." + pet.get_level() + " " + npc.get_nameid()
						+ "]");
			}
		}

		// ????????????????????????
		if (isIdentified() && getItem().getItemId() == 20383) {
			name.append(" [" + getChargeCount() + "]");
		}
		// ???????????????????????? end
		/*
		 * if (isIdentified() && getItem().getItemId() == 20288) {
		 * name.append(" [" + getChargeCount() + "]"); }
		 */

		// ????????????
		/*
		 * if ((getItem().getItemId() == 40001 || getItem().getItemId() == 40002
		 * || getItem().getItemId() == 40004 || getItem().getItemId() == 40005)
		 * && getEnchantLevel() != 0) { name.append(" ($10)"); } if
		 * ((getItem().getItemId() == 40001 || getItem().getItemId() == 40002)
		 * && getChargeCount() <= 0) { name.append(" ($11)"); }
		 */
		// ???????????? end

		/*
		 * if (isEquipped()) { if (itemType2 == 1) { name.append(" ($9)"); //
		 * ??????(Armed) } else if (itemType2 == 2) { name.append(" ($117)"); //
		 * ??????(Worn) } }
		 */
		return name.toString();
	}

	/**
	 * LOG???
	 */
	public String getNumberedLogViewName() {
		StringBuilder name = new StringBuilder(getViewNumberedName());
		// int itemType2 = getItem().getType2();

		if (getItem().getItemId() == 40314 || // ??????????????????????????????
				getItem().getItemId() == 40316) {
			L1Pet pet = PetTable.getInstance().getTemplate(getId());
			if (pet != null) {
				L1Npc npc = NpcTable.getInstance().getTemplate(pet.get_npcid());
				name.append("[Lv." + pet.get_level() + " " + npc.get_nameid()
						+ "]");
			}
		}

		// ????????????????????????
		if (isIdentified() && getItem().getItemId() == 20383) {
			name.append(" [" + getChargeCount() + "]");
		}
		// ???????????????????????? end
		/*
		 * if (isIdentified() && getItem().getItemId() == 20288) {
		 * name.append(" [" + getChargeCount() + "]"); }
		 */

		// ????????????
		/*
		 * if ((getItem().getItemId() == 40001 || getItem().getItemId() == 40002
		 * || getItem().getItemId() == 40004 || getItem().getItemId() == 40005)
		 * && getEnchantLevel() != 0) { name.append(" ($10)"); } if
		 * ((getItem().getItemId() == 40001 || getItem().getItemId() == 40002)
		 * && getChargeCount() <= 0) { name.append(" ($11)"); }
		 */
		// ???????????? end

		/*
		 * if (isEquipped()) { if (itemType2 == 1) { name.append(" ($9)"); //
		 * ??????(Armed) } else if (itemType2 == 2) { name.append(" ($117)"); //
		 * ??????(Worn) } }
		 */
		return name.toString();
	}

	/**
	 * LOG???
	 * 
	 * @return
	 */
	public String getLogViewName() {
		return getNumberedLogViewName(_count);
	}

	/**
	 * ?????????????????????????????????????????????????????????<br>
	 * ???:+10 ????????? (??????)
	 */
	public String getViewName() {
		return getNumberedViewName(_count);
	}

	/**
	 * ???????????????????????????????????????????????????<br>
	 * ???:?????????(250) / +6 ?????????
	 */
	public String getLogName() {
		return getNumberedName(_count);
	}

	/**
	 * ??????LOG???
	 */
	public String getViewNumberedName(long _count2) {
		StringBuilder name = new StringBuilder();
		if (getItem().getItemId() != 40001 && getItem().getItemId() != 40002
				&& getItem().getItemId() != 40004
				&& getItem().getItemId() != 40005) { // ??????????????????
			if (getItem().getType2() == 1) { // ??????
				int attrEnchantLevel = getAttrEnchantLevel();
				if (attrEnchantLevel > 0) {
					String attrStr = null;
					switch (getAttrEnchantKind()) {
					case 1: // ???
						if (attrEnchantLevel == 1) {
							attrStr = "??????";
						} else if (attrEnchantLevel == 2) {
							attrStr = "??????";
						} else if (attrEnchantLevel == 3) {
							attrStr = "??????";
						}
						break;
					case 2: // ???
						if (attrEnchantLevel == 1) {
							attrStr = "??????";
						} else if (attrEnchantLevel == 2) {
							attrStr = "??????";
						} else if (attrEnchantLevel == 3) {
							attrStr = "??????";
						}
						break;
					case 4: // ???
						if (attrEnchantLevel == 1) {
							attrStr = "??????";
						} else if (attrEnchantLevel == 2) {
							attrStr = "??????";
						} else if (attrEnchantLevel == 3) {
							attrStr = "??????";
						}
						break;
					case 8: // ???
						if (attrEnchantLevel == 1) {
							attrStr = "??????";
						} else if (attrEnchantLevel == 2) {
							attrStr = "??????";
						} else if (attrEnchantLevel == 3) {
							attrStr = "??????";
						}
						break;
					default:
						break;
					}
					name.append(attrStr + " ");
				}
			}
			if (getEnchantLevel() > 0) {
				name.append("+" + getEnchantLevel() + " ");
			} else if (getEnchantLevel() < 0) {
				name.append(String.valueOf(getEnchantLevel()) + " ");
			}
		}
		name.append(_item.getName());

		if (getItem().getMaxChargeCount() > 0 && getItem().getItemId() != 40001
				&& getItem().getItemId() != 40002
				&& getItem().getItemId() != 40003
				&& getItem().getItemId() != 40004
				&& getItem().getItemId() != 40005) { // ??????????????????
			name.append(" (" + getChargeCount() + ")");
		}
		if (getItem().getclassname().equals("Honor_Reel")) {
			if (getChargeCount() > 0) {
				name.append("???" + getChargeCount() + "???");
			}
		}
		if (isSeal()) {
			name.append(" *???*");
		}

		// ??????
		if (getItem().getItemId() == 40309) { // ?????????????????????
			// L1Npc npc = NpcTable.getInstance().getTemplate(getGamNpcId());
			name.append("(???" + getGamNo() + "???-" + getGamNpcName() + ")");
			// name.append("(" + getGamNo() + "-" + getGamNpcId() + ")");
		}

		if (_count2 > 1) {
			name.append(" (" + _count2 + ")");
		}

		return name.toString();
	}

	/**
	 * ??????LOG???
	 */
	public String getViewNumberedName() {
		StringBuilder name = new StringBuilder();
		if (getItem().getItemId() != 40001 && getItem().getItemId() != 40002
				&& getItem().getItemId() != 40004
				&& getItem().getItemId() != 40005) { // ??????????????????
			if (getItem().getType2() == 1) { // ??????
				int attrEnchantLevel = getAttrEnchantLevel();
				if (attrEnchantLevel > 0) {
					String attrStr = null;
					switch (getAttrEnchantKind()) {
					case 1: // ???
						if (attrEnchantLevel == 1) {
							attrStr = "??????";
						} else if (attrEnchantLevel == 2) {
							attrStr = "??????";
						} else if (attrEnchantLevel == 3) {
							attrStr = "??????";
						}
						break;
					case 2: // ???
						if (attrEnchantLevel == 1) {
							attrStr = "??????";
						} else if (attrEnchantLevel == 2) {
							attrStr = "??????";
						} else if (attrEnchantLevel == 3) {
							attrStr = "??????";
						}
						break;
					case 4: // ???
						if (attrEnchantLevel == 1) {
							attrStr = "??????";
						} else if (attrEnchantLevel == 2) {
							attrStr = "??????";
						} else if (attrEnchantLevel == 3) {
							attrStr = "??????";
						}
						break;
					case 8: // ???
						if (attrEnchantLevel == 1) {
							attrStr = "??????";
						} else if (attrEnchantLevel == 2) {
							attrStr = "??????";
						} else if (attrEnchantLevel == 3) {
							attrStr = "??????";
						}
						break;
					default:
						break;
					}
					name.append(attrStr + " ");
				}
			}
			if (getEnchantLevel() > 0) {
				name.append("+" + getEnchantLevel() + " ");
			} else if (getEnchantLevel() < 0) {
				name.append(String.valueOf(getEnchantLevel()) + " ");
			}
		}
		name.append(_item.getName());

		if (getItem().getMaxChargeCount() > 0 && getItem().getItemId() != 40001
				&& getItem().getItemId() != 40002
				&& getItem().getItemId() != 40003
				&& getItem().getItemId() != 40004
				&& getItem().getItemId() != 40005) { // ??????????????????
			name.append(" (" + getChargeCount() + ")");
		}
		if (getItem().getclassname().equals("Honor_Reel")) {
			if (getChargeCount() > 0) {
				name.append("???" + getChargeCount() + "???");
			}
		}
		if (isSeal()) {
			name.append(" *???*");
		}
		// ??????
		if (getItem().getItemId() == 40309) { // ?????????????????????
			// L1Npc npc = NpcTable.getInstance().getTemplate(getGamNpcId());
			name.append("(???" + getGamNo() + "???-" + getGamNpcName() + ")");
			// name.append("(" + getGamNo() + "-" + getGamNpcId() + ")");
		}

		/*
		 * if (_count2 > 1) { name.append(" (" + _count2 + ")"); }
		 */

		return name.toString();
	}

	/**
	 * ?????????????????????????????????????????????????????????????????????????????????
	 */
	public String getNumberedName(long _count2) {
		StringBuilder name = new StringBuilder();

		if (isIdentified()) {
			if (getItem().getItemId() != 40001
					&& getItem().getItemId() != 40002
					&& getItem().getItemId() != 40004
					&& getItem().getItemId() != 40005) { // ??????????????????
				if (getItem().getType2() == 1 || getItem().getType2() == 0) { // ??????
					int attrEnchantLevel = getAttrEnchantLevel();
					if (attrEnchantLevel > 0) {
						String attrStr = null;
						switch (getAttrEnchantKind()) {
						case 1: // ???
							if (attrEnchantLevel == 1) {
								attrStr = "??????";
							} else if (attrEnchantLevel == 2) {
								attrStr = "??????";
							} else if (attrEnchantLevel == 3) {
								attrStr = "??????";
							}
							break;
						case 2: // ???
							if (attrEnchantLevel == 1) {
								attrStr = "??????";
							} else if (attrEnchantLevel == 2) {
								attrStr = "??????";
							} else if (attrEnchantLevel == 3) {
								attrStr = "??????";
							}
							break;
						case 4: // ???
							if (attrEnchantLevel == 1) {
								attrStr = "??????";
							} else if (attrEnchantLevel == 2) {
								attrStr = "??????";
							} else if (attrEnchantLevel == 3) {
								attrStr = "??????";
							}
							break;
						case 8: // ???
							if (attrEnchantLevel == 1) {
								attrStr = "??????";
							} else if (attrEnchantLevel == 2) {
								attrStr = "??????";
							} else if (attrEnchantLevel == 3) {
								attrStr = "??????";
							}
							break;
						default:
							break;
						}
						name.append(attrStr + " ");
					}
				}
				if (getEnchantLevel() > 0) {
					name.append("+" + getEnchantLevel() + " ");
				} else if (getEnchantLevel() < 0) {
					name.append(String.valueOf(getEnchantLevel()) + " ");
				}
			}
		}
		name.append(_item.getNameId());
		if (isIdentified()) {
			if (getItem().getMaxChargeCount() > 0
					&& getItem().getItemId() != 40001
					&& getItem().getItemId() != 40002
					&& getItem().getItemId() != 40003
					&& getItem().getItemId() != 40004
					&& getItem().getItemId() != 40005) { // ??????????????????
				name.append(" (" + getChargeCount() + ")");
			}
			if (getItem().getclassname().equals("Honor_Reel")) {
				if (getChargeCount() > 0) {
					name.append("???" + getChargeCount() + "???");
				}
			}
			if (isSeal()) {
				name.append(" *???*");
			}
		}
		// ??????
		if (getItem().getItemId() == 40309) { // ?????????????????????
			// L1Npc npc = NpcTable.getInstance().getTemplate(getGamNpcId());
			name.append("(???" + getGamNo() + "???-" + getGamNpcName() + ")");
			// name.append("(" + getGamNo() + "-" + getGamNpcId() + ")");
		}

		if (_count2 > 1) {
			name.append(" (" + _count2 + ")");
		}

		return name.toString();
	}

	/**
	 * ???????????????????????????????????????????????????????????????????????????????????????????????????????????????
	 */
	public byte[] getStatusBytes() {
		int itemType2 = getItem().getType2();
		int itemId = getItemId();
		BinaryOutputStream os = new BinaryOutputStream();
		int ringSp = 0;
		if (itemType2 == 2 && getItem().getType() == 9 && getEnchantLevel() > 0) {
			ringSp = getEnchantLevel();
		}
		if (itemType2 == 0) { // etcitem
			if (getItem().getItemId() == 99994) {
				os.writeC(0x27);
				os.writeSA(" ??????:?????????????????????");
				//os.writeS(String.format("$15197"));
				os.writeC(0x27);
				os.writeSA("??????:????????????x300");
				//os.writeS(String.format("$15198"));
				os.writeC(0x27);
				os.writeSA("??????:????????????x10");
				//os.writeS(String.format("$15199"));
			}
			if (getItem().getItemId() == 10113) {
				os.writeC(0x27);
				os.writeSA(" ??????????????????????????????");
				//os.writeS(String.format("$15200"));
				os.writeC(0x27);
				os.writeSA("?????????????????????+1");
				//os.writeS(String.format("$15201"));
			}
			if (getItem().getItemId() == 10048) {
				os.writeC(0x27);
				// os.writeSA(" ?????????????????????1-4??????");
				os.writeS(String.format("$15202"));
			}
			if (getItem().getItemId() == 11015) {
				os.writeC(0x27);
				os.writeSA(" 10%????????????");
				os.writeC(0x27);
				os.writeSA("10%????????????");
				os.writeC(0x27);
				os.writeSA("10%????????????");
				os.writeC(0x27);
				os.writeSA("10%????????????");
				os.writeC(0x27);
				os.writeSA("10%????????????");
				os.writeC(0x27);
				os.writeSA("10%????????????");
				os.writeC(0x27);
				os.writeSA("10%????????????");
				os.writeC(0x27);
				os.writeSA("10%????????????");
				os.writeC(0x27);
				os.writeSA("10%????????????");
				os.writeC(0x27);
				os.writeSA("10%????????????");
			}
			if (getItem().getItemId() == 11016) {
				os.writeC(0x27);
				os.writeSA(" 12%????????????");
				os.writeC(0x27);
				os.writeSA("12%????????????");
				os.writeC(0x27);
				os.writeSA("12%????????????");
				os.writeC(0x27);
				os.writeSA("12%????????????");
				os.writeC(0x27);
				os.writeSA("12%????????????");
				os.writeC(0x27);
				os.writeSA("12%????????????");
				os.writeC(0x27);
				os.writeSA("12%????????????");
				os.writeC(0x27);
				os.writeSA("12%????????????");
			}

			switch (getItem().getType()) {
			case 2: // light
				os.writeC(22); // ?????????
				os.writeH(getItem().getLightRange());
				break;
			case 7: // food
				os.writeC(21);
				// ??????
				os.writeH(getItem().getFoodVolume());
				break;
			case 0: // arrow
			case 15: // sting
				os.writeC(1); // ?????????
				os.writeC(getItem().getDmgSmall());
				os.writeC(getItem().getDmgLarge());
				break;
			default:
				os.writeC(23); // ??????
				break;
			}
			os.writeC(getItem().getMaterial());
			os.writeD(getWeight());

			/*
			 * final ArrayList<L1DollExecutor> dolls =
			 * DollXiLianTable.get().getDollTypes(this.getId()); if (dolls !=
			 * null && !dolls.isEmpty()){ os.writeC(0x27); os.writeS(":");
			 * for(final L1DollExecutor dollType : dolls){ os.writeC(0x27);
			 * os.writeS
			 * (String.format("%s %d",dollType.getNameId(),dollType.getValue1
			 * ())); } }
			 */

		} else if (itemType2 == 1 || itemType2 == 2) { // weapon | armor
			int enchantsaf_hp = 0;
			int enchantsaf_mp = 0;
			int enchantsaf_mr = 0;
			String enchantsaf_returndmg = null;
			if (getItem().getItemId() == 25063
					|| getItem().getItemId() == 25064) {
				int new_enchant = getEnchantLevel()
						- getItem().get_safeenchant();
				if (new_enchant == 1) {// +5
					enchantsaf_returndmg = "$15180";
					enchantsaf_hp = 10;
					enchantsaf_mp = 10;
					enchantsaf_mr = 2;
				} else if (new_enchant == 2) {// +6
					enchantsaf_returndmg = "$15181";
					enchantsaf_hp = 20;
					enchantsaf_mp = 20;
					enchantsaf_mr = 4;
				} else if (new_enchant == 3) {// +7
					enchantsaf_returndmg = "$15182";
					enchantsaf_hp = 30;
					enchantsaf_mp = 30;
					enchantsaf_mr = 6;
				} else if (new_enchant == 4) {// +8
					enchantsaf_returndmg = "$15183";
					enchantsaf_hp = 40;
					enchantsaf_mp = 40;
					enchantsaf_mr = 8;
				} else if (new_enchant == 5) {// +9
					enchantsaf_returndmg = "$15184";
					enchantsaf_hp = 50;
					enchantsaf_mp = 50;
					enchantsaf_mr = 10;
				} else if (new_enchant == 6) {// +9
					enchantsaf_returndmg = "$15184";
					enchantsaf_hp = 60;
					enchantsaf_mp = 60;
					enchantsaf_mr = 12;
				} else if (new_enchant == 7) {// +9
					enchantsaf_returndmg = "$15184";
					enchantsaf_hp = 70;
					enchantsaf_mp = 70;
					enchantsaf_mr = 14;
				} else if (new_enchant == 8) {// +9
					enchantsaf_returndmg = "$15184";
					enchantsaf_hp = 80;
					enchantsaf_mp = 80;
					enchantsaf_mr = 16;
				} else if (new_enchant == 9) {// +9
					enchantsaf_returndmg = "$15184";
					enchantsaf_hp = 80;
					enchantsaf_mp = 80;
					enchantsaf_mr = 20;
				}
			}
			if (itemType2 == 1) { // weapon
				// ?????????
				os.writeC(1);
				os.writeC(getItem().getDmgSmall());
				os.writeC(getItem().getDmgLarge());
				os.writeC(getItem().getMaterial());
				os.writeD(getWeight());

			} else if (itemType2 == 2) { // armor
				// AC
				os.writeC(19);
				int ac = ((L1Armor) getItem()).get_ac() + getItemEnchant(0);
				if (ac < 0) {
					ac = ac - ac - ac;
				}
				os.writeC(ac);
				os.writeC(getItem().getMaterial());
				os.writeC(-1);// CNOP??????
				os.writeD(getWeight());
			}
			// ?????????
			if (getEnchantLevel() != 0) {
				if (itemType2 == 1) {
					os.writeC(2);
					os.writeC(getEnchantLevel());
				} else if (itemType2 == 2
						&& (getItem().getType() == 9 || getItem().getType() == 15) == false) {
					os.writeC(2);
					os.writeC(getEnchantLevel());
				}
			}
			// ?????????
			if (get_durability() != 0) {
				os.writeC(3);
				os.writeC(get_durability());
			}

			// ????????????
			int bit = 0;
			bit |= getItem().isUseRoyal() ? 1 : 0;
			bit |= getItem().isUseKnight() ? 2 : 0;
			bit |= getItem().isUseElf() ? 4 : 0;
			bit |= getItem().isUseMage() ? 8 : 0;
			bit |= getItem().isUseDarkelf() ? 16 : 0;
			bit |= getItem().isUseDragonknight() ? 32 : 0;
			bit |= getItem().isUseIllusionist() ? 64 : 0;
			// bit |= getItem().isUseHiPet() ? 64 : 0; // ???????????????
			os.writeC(7);
			os.writeC(bit);
			if (getItem().getItemId() == 21200) {
				os.writeC(0x27);
				os.writeSA("****************");
				if (getUpdateHP() > 0) {
					os.writeC(0x27);
					os.writeSA("??????HP +" + getUpdateHP());
 				}
				if (get_updateMP() > 0) {
					os.writeC(0x27);
					os.writeSA("??????MP +" + get_updateMP());
 				}
				if (get_updateMPR() > 0) {
					os.writeC(0x27);
					os.writeSA("???????????? +" + get_updateMPR());
 				}
				if (get_updateHPR() > 0) {
					os.writeC(0x27);
					os.writeSA("???????????? +" + get_updateHPR());
 				}
				if (get_updateDMG() > 0) {
					os.writeC(0x27);
					os.writeSA("?????????????????? +" + get_updateDMG());
 				}
				if (get_updateHOTDMG() > 0) {
					os.writeC(0x27);
					os.writeSA("?????????????????? +" + get_updateHOTDMG());
 				}
				if (get_updateBOWDMG() > 0) {
					os.writeC(0x27);
					os.writeSA("?????????????????? +" + get_updateBOWDMG());
 				}
				if (get_updateHOTBOWDMG() > 0) {
					os.writeC(0x27);
					os.writeSA("?????????????????? +" + get_updateHOTBOWDMG());
 				}
				if (get_updateSP() > 0) {
					os.writeC(0x27);
					os.writeSA("???????????? +" + get_updateSP());
 				}
			}
			// ????????????
			if (getItem().isTwohandedWeapon()) {
				os.writeC(4);
			}
			int HitModifier = getItem().getHitModifier();
			int DmgModifier = getItem().getDmgModifier();
			// ????????????
			if (HitModifier != 0) {
				os.writeC(5);
				os.writeC(HitModifier);
			}
			// ????????????
			if (DmgModifier != 0) {
				os.writeC(6);
				os.writeC(DmgModifier);
			}
			// ????????????
			if (getItem().getBowHitRate() != 0) {
				os.writeC(24);
				os.writeC(getItem().getBowHitRate());
			}
			// MP??????
			if (itemId == 126 || itemId == 127 || getItem().isManaItem()) { // ???????????????????????????????????????????????????
				// ??????getItem().isManaItem()
				os.writeC(16);
			}
			// STR~CHA
			int addstr = getItem().get_addstr() + getItemEnchant(5);
			if (addstr != 0) {
				os.writeC(8);
				os.writeC(addstr);
			}
			int adddex = getItem().get_adddex() + getItemEnchant(6);
			if (adddex != 0) {
				os.writeC(9);
				os.writeC(adddex);
			}
			int addcon = getItem().get_addcon() + getItemEnchant(8);
			if (addcon != 0) {
				os.writeC(10);
				os.writeC(addcon);
			}
			int addwis = getItem().get_addwis() + getItemEnchant(7);
			if (addwis != 0) {
				os.writeC(11);
				os.writeC(addwis);
			}
			int addint = getItem().get_addint() + getItemEnchant(10);
			if (addint != 0) {
				os.writeC(12);
				os.writeC(addint);
			}
			int addcha = getItem().get_addcha() + getItemEnchant(9);
			if (addcha != 0) {
				os.writeC(13);
				os.writeC(addcha);
			}
			int addhp = getItem().get_addhp() + enchantsaf_hp
					+ getItemEnchant(1);
			if (getItem().getType2() == 2 && this.getBless() == 0) {
				addhp += 20;
			}
			if (addhp != 0) {
				os.writeC(31);
				os.writeH(addhp);
			}
			int addmp = getItem().get_addmp() + enchantsaf_mp
					+ getItemEnchant(2);
			if (addmp != 0) {
				// os.writeC(32);
				// os.writeC(addmp);
				os.writeC(0x27);
				os.writeS(String.format("$1673 %d", addmp));
			}
			// MR
			int addmr = getMr() + enchantsaf_mr + getItemEnchant(12);
			if (addmr != 0) {
				os.writeC(15);
				os.writeH(addmr);
			}
			// SP(??????)
			int addsp = getItem().get_addsp() + ringSp + getItemEnchant(11);
			if (addsp != 0) {
				os.writeC(17);
				os.writeC(addsp);
			}
			final int addhpr = getItem().get_addhpr() + getItemEnchant(3);
			if (addhpr != 0) {
				os.writeC(0x25);
				os.writeC(addhpr);
			}
			final int addmpr = getItem().get_addmpr() + getItemEnchant(4);
			if (addmpr != 0) {
				os.writeC(0x26);
				os.writeC(addmpr);
			}
			// ????????????
			if (getItem().isHasteItem()) {
				os.writeC(18);
			}
			// ????????????
			if (getItem().get_defense_fire() != 0) {
				os.writeC(27);
				os.writeC(getItem().get_defense_fire());
			}
			// ????????????
			if (getItem().get_defense_water() != 0) {
				os.writeC(28);
				os.writeC(getItem().get_defense_water());
			}
			// ????????????
			if (getItem().get_defense_wind() != 0) {
				os.writeC(29);
				os.writeC(getItem().get_defense_wind());
			}
			// ????????????
			if (getItem().get_defense_earth() != 0) {
				os.writeC(30);
				os.writeC(getItem().get_defense_earth());
			}
			if (itemType2 == 1) {
				L1WilliamWeaponSkill weaponSkill = WeaponSkill.getInstance()
						.getTemplate(getItem().getItemId());
				if (weaponSkill != null) {
					int rnd = weaponSkill.getProbability();
					int safeenchant = getEnchantLevel()
							- getItem().get_safeenchant();
					if (safeenchant > 0) {
						rnd += safeenchant * 2;
					}
					if (rnd > 0) {
						os.writeC(0x27);
						os.writeS(String.format("$15178 %d", rnd));
					}
				}
			}
			if (enchantsaf_returndmg != null) {
				os.writeC(0x27);
				os.writeS(enchantsaf_returndmg);
			}
			final int addDmgduction = getItemEnchant(13);
			if (addDmgduction > 0) {
				os.writeC(0x27);
				os.writeS(String.format("$15187 %d", addDmgduction));
			}
			if (getItem().get_safeenchant() >= 0) {
				os.writeC(0x27);
				os.writeS(String.format("$15186 %d", getItem()
						.get_safeenchant()));
			}
			if (getItem().getItemId() == 21200) {
				os.writeC(0x27);
				os.writeSA("****************");
			}
			if (getItem().getItemId() == 70030) {
				os.writeC(0x27);
				os.writeSA("PVP????????????");
				//os.writeS(String.format("$15189"));
				os.writeC(0x27);
				os.writeSA("??????????????????");
				//os.writeS(String.format("$15190"));
				//os.writeC(0x27);
				//os.writeSA("?????? ????????????20%");
				//os.writeS(String.format("$15191"));
			}
			if (getItem().getItemId() == 500052) {
				os.writeC(0x27);
				os.writeSA("??????????????????");
				//os.writeS(String.format("$15192"));
				os.writeC(0x27);
				os.writeSA("??????????????????");
				//os.writeS(String.format("$15193"));
			}
			if (getItem().getItemId() == 25063) {
				os.writeC(0x27);
				os.writeSA("????????????????????????");
				//os.writeS(String.format("$15194"));
			}
			if (getUpdatePVP() > 0) {
				os.writeC(0x27);
				os.writeSA("PVP?????? + " + getUpdatePVP());
				//os.writeS(String.format("$15195 %d", getUpdatePVP()));
			}
			if (getUpdatePVE() > 0) {
				os.writeC(0x27);
				os.writeSA("PVE?????? + " + getUpdatePVE());
				//os.writeS(String.format("$15196 %d", getUpdatePVE()));
			}
		}
		return os.getBytes();
	}

	// ???????????????????????????
	private int _Enchant = 0;

	public int getEnchant() {
		return _Enchant;
	}

	public void setEnchant(int i) {
		_Enchant = i;
	}

	private int _HolyEnchant = 0;

	public int getHolyEnchant() {
		return _HolyEnchant;
	}

	public void setHolyEnchant(int i) {
		_HolyEnchant = i;
	}

	private int _Hit = 0;

	public int getHit() {
		return _Hit;
	}

	public void setHit(int i) {
		_Hit = i;
	}

	/**
	 * ????????????????????????
	 * 
	 * @return
	 */
	public int getGamNo() {
		return _gamNo;
	}

	/**
	 * ????????????????????????
	 * 
	 * @param i
	 */
	public void setGamNo(int i) {
		_gamNo = i;
	}

	/**
	 * ??????????????????NPC??????
	 * 
	 * @return
	 */
	public int getGamNpcId() {
		return _gamNpcId;
	}

	/**
	 * ??????????????????NPC??????
	 * 
	 * @param i
	 */
	public void setGamNpcId(int i) {
		_gamNpcId = i;
	}

	public String getGamNpcName() {
		return _gamNpcName;
	}

	public void setGamNpcName(String name) {
		_gamNpcName = name;
	}

	/*
	 * // ?????????????????????????????? private Timestamp _time = null;
	 *//**
	 * ????????????????????????????????????
	 * 
	 * @param skilltime
	 */
	/*
	 * public void set_time(Timestamp time) { _time = time; }
	 *//**
	 * ??????????????????????????????
	 * 
	 * @return _skilltime
	 */
	/*
	 * public Timestamp get_time() { return _time; }
	 */

	public final void setSkillEnchant(L1PcInstance pc, int skillId,
			int skillTime) {
		if (_timer != null) { // ???????????????????????????
			_timer.cancel();
			int type = getItem().getType2();
			int item_id = getItem().getItemId();
			setEnchant(0);
			if (pc != null && pc.getInventory().checkItem(item_id)) {
				if (type == 2 && isEquipped()) { // ???????????????
					pc.getEquipSlot().OnChanceAc();
					pc.sendPackets(new S_OwnCharStatus(pc));
				}
			}
			setHolyEnchant(0);
			setHit(0);
			_isRunning = false;
			_timer = null;
		}

		if (!_isRunning) { // ???????????????????????????
			switch (skillId) {
			case L1SkillId.HOLY_WEAPON: // ??????
				setHolyEnchant(1);
				setHit(1);
				break;

			case L1SkillId.ENCHANT_WEAPON: // ??????
				setEnchant(2);
				break;

			case L1SkillId.BLESSED_ARMOR: // ??????
				setEnchant(3);
				/*
				 * if(isEquipped()) { pc.addAc(-3); pc.sendPackets(new
				 * S_OwnCharStatus(pc)); }
				 */
				pc.getEquipSlot().OnChanceAc();
				pc.sendPackets(new S_OwnCharStatus(pc));
				break;

			case L1SkillId.BLESS_WEAPON: // ??????
				setEnchant(2);
				setHit(2);
				break;

			case L1SkillId.SHADOW_FANG: // ??????
				setEnchant(5);
				break;

			default:
				break;
			}

			_owner = pc;
			_timer = new EnchantTimer();
			(new Timer()).schedule(_timer, skillTime);
			_isRunning = true;
		}
	}

	// ??????????????????????????? end

	// ?????????
	public final void startLight(L1PcInstance pc, L1ItemInstance item) {
		if (item.getEnchantLevel() == 0) {
			if (_lightTimer != null) {
				_lightTimer.cancel();
				_lightTimer = null;
			}

			_owner = pc;
			_Item = item;
			_lightTimer = new LightTimer();
			(new Timer()).schedule(_lightTimer, 4000);
		} else {
			if (_lightTimer != null) {
				_lightTimer.cancel();
				_lightTimer = null;
			}
		}
	}

	// ????????? end

	// ????????????
	private void Light(L1PcInstance pc, L1ItemInstance item) {
		_owner = pc;
		item.startLight(_owner, item);

		if (item.getEnchantLevel() != 0) {
			item.setEnchantLevel(0);
		}

		_owner.getInventory().updateItem(item, L1PcInventory.COL_ENCHANTLVL);
		_owner.sendPackets(new S_ItemName(item));

		if (_owner.hasSkillEffect(2)) {// ?????????
			_owner.setPcLight(14);
		}

		for (Object Light : _owner.getInventory().getItems()) {
			L1ItemInstance OwnLight = (L1ItemInstance) Light;
			if ((OwnLight.getItem().getItemId() == 40001
					|| OwnLight.getItem().getItemId() == 40002
					|| OwnLight.getItem().getItemId() == 40004 || OwnLight
					.getItem().getItemId() == 40005)
					&& OwnLight.getEnchantLevel() != 0) {
				if (_owner.getPcLight() < OwnLight.getItem().getLightRange()) {
					_owner.setPcLight(OwnLight.getItem().getLightRange());
				}
			}
		}

		_owner.sendPackets(new S_Light(_owner.getId(), _owner.getPcLight()));
		if (!_owner.isInvisble() && item.getItem().getItemId() != 40004) {// ?????????????????????????????????
			_owner.broadcastPacket(new S_Light(_owner.getId(), _owner
					.getPcLight()));
		}
	}

	// ????????????
	private boolean _isbroad = false;

	public void setBroad(final boolean flag) {
		_isbroad = flag;
	}

	public boolean isBroad() {
		return _isbroad;
	}

	private boolean _isseal = false;

	public void setSeal(final boolean flag) {
		_isseal = flag;
	}

	public boolean isSeal() {
		return _isseal;
	}

	public void setOverSeal(Timestamp t) {
		_overSeal = t;
	}

	public Timestamp getOverSeal() {
		return _overSeal;
	}

	private int _equipWindow;

	public final int getEquipWindow() {
		return _equipWindow;
	}

	public final void setEquipWindow(final int i) {
		_equipWindow = i;
	}

	private long _dropTimestamp = 0;

	public void setDropTimestamp(final long nowtime) {
		_dropTimestamp = nowtime;
	}

	public long getDropTimestamp() {
		return _dropTimestamp;
	}

	private String _killDeathName = null;

	public void setKillDeathName(final String kname) {
		_killDeathName = kname;
	}

	public String getKillDeathName() {
		return _killDeathName;
	}

	private int _dropObjId = 0;

	public void setDropObjId(final int objId) {
		_dropObjId = objId;
	}

	public int getDropObjId() {
		return _dropObjId;
	}

	private int _bless; // ??? ????????????

	public int getBless() {
		return _bless;
	}

	public void setBless(int i) {
		_bless = i;
	}

	/**
	 * ????????????????????????<br>
	 * 0:ac 1:hp 2:mp 3:hpr 4:mpr 5:str 6:dex 7:wis 8:con 9:cha 10:intel 11:sp
	 * 12:mr 13:dmgReduction
	 * 
	 * @param type
	 * @return
	 */
	public int getItemEnchant(final int type) {
		int value = 0;
		final L1EnchantDmgreduction itemEnchant = EnchantDmgReductionTable
				.get().getEnchantDmgReduction(_item.getItemId(),
						getEnchantLevel());
		if (itemEnchant != null) {
			switch (type) {
			case 0:
				value = itemEnchant.get_ac();
				break;
			case 1:
				value = itemEnchant.get_hp();
				break;
			case 2:
				value = itemEnchant.get_mp();
				break;
			case 3:
				value = itemEnchant.get_hpr();
				break;
			case 4:
				value = itemEnchant.get_mpr();
				break;
			case 5:
				value = itemEnchant.get_str();
				break;
			case 6:
				value = itemEnchant.get_dex();
				break;
			case 7:
				value = itemEnchant.get_wis();
				break;
			case 8:
				value = itemEnchant.get_con();
				break;
			case 9:
				value = itemEnchant.get_cha();
				break;
			case 10:
				value = itemEnchant.get_Intel();
				break;
			case 11:
				value = itemEnchant.get_sp();
				break;
			case 12:
				value = itemEnchant.get_mr();
				break;
			case 13:
				value = itemEnchant.get_dmgReduction();
				break;
			default:
				break;
			}
		}
		return value;
	}

	private boolean _tradable = false; // ??? ????????????????????????

	public boolean isTradable() {
		return _tradable;
	}

	public void setTradable(boolean flag) {
		_tradable = flag;
	}

	private boolean _ondurability = false;

	public boolean isOndurability() {
		return _ondurability;
	}

	public void setOndurability(final boolean durability) {
		_ondurability = durability;
	}

	// ??????????????????????????????
	private Timestamp _time = null;

	/**
	 * ????????????????????????????????????
	 * 
	 * @param skilltime
	 */
	public void set_time(Timestamp time) {
		_time = time;
	}

	/**
	 * ??????????????????????????????
	 * 
	 * @return _skilltime
	 */
	public Timestamp get_time() {
		return _time;
	}

	// ???????????????OBJID
	private int _char_objid = -1;

	/**
	 * ?????????????????????OBJID
	 * 
	 */
	public void set_char_objid(int char_objid) {
		_char_objid = char_objid;
	}

	/**
	 * ???????????????OBJID
	 * 
	 */
	public int get_char_objid() {
		return _char_objid;
	}

	private L1CharaterTrade _itemCharaterTrade = null;

	public void setItemCharaterTrade(final L1CharaterTrade charaterTrade) {
		_itemCharaterTrade = charaterTrade;
	}

	public L1CharaterTrade getItemCharaterTrade() {
		return _itemCharaterTrade;
	}

	private boolean _isXiLianing = false;

	public boolean isXiLianIng() {
		return _isXiLianing;
	}

	public void setXiLianIng(final boolean b) {
		_isXiLianing = b;
	}

	private boolean _isGmDrop = false;

	public boolean isGmDrop() {
		return _isGmDrop;
	}

	public void setGmDrop(final boolean b) {
		_isGmDrop = b;
	}

	/**
	 * PVP????????????
	 * 
	 * @return _updateHitModifier
	 */
	private int _updatePVP = 0;

	public int getUpdatePVP() {
		return _updatePVP;
	}

	public void setUpdatePVP(int i) {
		_updatePVP = i;
	}

	/**
	 * PVE????????????
	 * 
	 * @return _updateDmgModifier
	 */
	private int _updatePVE = 0;

	public int getUpdatePVE() {
		return _updatePVE;
	}

	public void setUpdatePVE(int i) {
		_updatePVE = i;
	}
	
	public int getUpdateHP() {
		return _updateHP;
	}

	public void setUpdateHP(int i) {
		_updateHP = i;
	}
	
	private int _updateHP = 0;
	
	public int get_updateMP() {
		return _updateMP;
	}

	public void set_updateMP(int _updateMP) {
		this._updateMP = _updateMP;
	}

	public int get_updateHPR() {
		return _updateHPR;
	}

	public void set_updateHPR(int _updateHPR) {
		this._updateHPR = _updateHPR;
	}

	public int get_updateMPR() {
		return _updateMPR;
	}

	public void set_updateMPR(int _updateMPR) {
		this._updateMPR = _updateMPR;
	}

	public int get_updateDMG() {
		return _updateDMG;
	}

	public void set_updateDMG(int _updateDMG) {
		this._updateDMG = _updateDMG;
	}

	public int get_updateBOWDMG() {
		return _updateBOWDMG;
	}

	public void set_updateBOWDMG(int _updateBOWDMG) {
		this._updateBOWDMG = _updateBOWDMG;
	}

	public int get_updateHOTDMG() {
		return _updateHOTDMG;
	}

	public void set_updateHOTDMG(int _updateHOTDMG) {
		this._updateHOTDMG = _updateHOTDMG;
	}

	public int get_updateHOTBOWDMG() {
		return _updateHOTBOWDMG;
	}

	public void set_updateHOTBOWDMG(int _updateHOTBOWDMG) {
		this._updateHOTBOWDMG = _updateHOTBOWDMG;
	}

	private int _updateMP = 0;
	
	private int _updateHPR = 0;
	
	private int _updateMPR = 0;
	
	private int _updateDMG = 0;
	
	private int _updateBOWDMG = 0;
	
	private int _updateHOTDMG = 0;
	
	private int _updateHOTBOWDMG = 0;
	
	private int _updateSP = 0;

	public int get_updateSP() {
		return _updateSP;
	}

	public void set_updateSP(int _updateSP) {
		this._updateSP = _updateSP;
	}
	
	/**
	 * ??????????????????
	 */
	private boolean _issbxz = false;

	public boolean issbxz() {
		return _issbxz;
	}

	public void setsbxz(final boolean b) {
		_issbxz = b;
	}

}