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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
//import java.io.BufferedWriter;
//import java.util.Collection;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.util.HashMap;
//import java.io.OutputStream;
import java.util.List;
import java.util.Map;
//import java.util.Map;
import java.util.Random;
//import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;

import l1j.server.AcceleratorChecker;
import l1j.server.Config;
import l1j.server.server.ActionCodes;
//import l1j.server.server.ClientThread;
import l1j.server.server.GeneralThreadPool;
//import l1j.server.server.PacketOutput;
import l1j.server.server.WarTimeController;
import l1j.server.server.WriteLogTxt;
//import l1j.server.server.command.GMCommands;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.datatables.ExpTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.MapsTable;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.datatables.SprTable;
import l1j.server.server.datatables.lock.CharSkillReading;
import l1j.server.server.mina.LineageClient;
import l1j.server.server.model.HpRegeneration;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Clan;
//import l1j.server.server.model.L1DwarfInventory;
import l1j.server.server.model.L1EquipmentSlot;
import l1j.server.server.model.L1ExcludingMailList;
import l1j.server.server.model.L1GamSpList;
import l1j.server.server.model.L1HateList;
import l1j.server.server.model.L1Inventory;
//import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Karma;
import l1j.server.server.model.L1Location;
//import l1j.server.server.model.L1Magic;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PCAction;
import l1j.server.server.model.L1Party;
import l1j.server.server.model.L1PcBlessEnchant;
import l1j.server.server.model.L1PcHealAI;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.L1PinkName;
// ??????,??????,??????,??????,???????????? 
// ??????,??????,??????,??????,????????????  end
import l1j.server.server.model.L1Quest;
import l1j.server.server.model.L1Spawn;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1TownLocation;
import l1j.server.server.model.L1War;
import l1j.server.server.model.MpRegeneration;
import l1j.server.server.model.MpRegenerationByDoll;
import l1j.server.server.model.classes.L1ClassFeature;
import l1j.server.server.model.gametime.L1GameTimeCarrier;
import l1j.server.server.model.guaji.L1PcAI;
import l1j.server.server.model.guaji.NpcMoveExecutor;
import l1j.server.server.model.guaji.pcMove;
//import l1j.server.server.model.monitor.L1PcExpMonitor;
import l1j.server.server.model.monitor.L1PcGhostMonitor;
import l1j.server.server.model.monitor.L1PcInvisDelay;
import l1j.server.server.model.poison.L1Poison2;
import l1j.server.server.model.poison.L1Poison3;
import l1j.server.server.model.poison.L1Poison4;
import l1j.server.server.model.poison.L1Poison6;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.*;
//import l1j.server.server.serverpackets.S_Disconnect; // ??????????????????
import l1j.server.server.templates.L1CharacterAdenaTrade;
import l1j.server.server.templates.L1FindShopSell;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1Pc;
import l1j.server.server.templates.L1PrivateShopBuyList;
import l1j.server.server.templates.L1PrivateShopSellList;
import l1j.server.server.templates.L1Skills;
import l1j.server.server.timecontroller.pc.AutoMagic;
//import l1j.server.server.templates.Tbs;
import l1j.server.server.utils.CalcStat;
//import l1j.william.PlayerSpeed;
//import l1j.william.L1WilliamPlayerSpeed;
//import l1j.william.L1WilliamSystemMessage;
import l1j.server.server.world.L1World;
import l1j.william.L1WilliamJiaRen;
import l1j.william.Reward;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


// Referenced classes of package l1j.server.server.model:
// L1Character, L1DropTable, L1Object, L1ItemInstance,
// L1World
//

public class L1PcInstance extends L1Character {
	private static final long serialVersionUID = 1L;

	public static final int CLASSID_KNIGHT_MALE = 61;
	public static final int CLASSID_KNIGHT_FEMALE = 48;
	public static final int CLASSID_ELF_MALE = 138;
	public static final int CLASSID_ELF_FEMALE = 37;
	public static final int CLASSID_WIZARD_MALE = 734;
	public static final int CLASSID_WIZARD_FEMALE = 1186;
	public static final int CLASSID_DARK_ELF_MALE = 2786;
	public static final int CLASSID_DARK_ELF_FEMALE = 2796;
	public static final int CLASSID_DRAGON_KNIGHT_MALE = 6658;
	public static final int CLASSID_DRAGON_KNIGHT_FEMALE = 6661;
	public static final int CLASSID_ILLUSIONIST_MALE = 6671;
	public static final int CLASSID_ILLUSIONIST_FEMALE = 6650;
	public static final int CLASSID_PRINCE = 0;
	public static final int CLASSID_PRINCESS = 1;
	private final Map<Integer, Integer> _uplevelList;// ??????????????????(??????/??????????????????)
	private static Random _random = new Random();
	// private final static Map<Integer, L1PcHpMp> _levelhpmpup = new
	// HashMap<Integer, L1PcHpMp>();
	// private final Map<Integer, Integer> _levelmpup = new HashMap<Integer,
	// Integer>();
	private final L1Inventory _tradewindow;
	private final ArrayList<Object> _tempObjects = new ArrayList<Object>();

	public void addTempObject(final Object obj) {
		_tempObjects.add(obj);
	}

	public void clearTempObjects() {
		_tempObjects.clear();
	}

	public ArrayList<Object> getTempObjects() {
		return _tempObjects;
	}

	private short _hpr = 0;
	private short _trueHpr = 0;

	public short getHpr() {
		return _hpr;
	}

	public void addHpr(final int i) {
		_trueHpr += i;
		_hpr = (short) Math.max(0, _trueHpr);
	}

	private short _mpr = 0;
	private short _trueMpr = 0;

	public short getMpr() {
		return _mpr;
	}

	public void addMpr(final int i) {
		_trueMpr += i;
		_mpr = (short) Math.max(0, _trueMpr);
	}

	public synchronized void startHpRegeneration() {
		/*
		 * final int INTERVAL = 1000;
		 * 
		 * if (!_hpRegenActive) { _hpRegen = new HpRegeneration(this);
		 * _regenTimer.scheduleAtFixedRate(_hpRegen, INTERVAL, INTERVAL);
		 * _hpRegenActive = true; }
		 */
		if (!_hpRegenActive) {
			if (_hpRegen == null) {
				_hpRegen = new HpRegeneration(this);
			}
			/*
			 * if (_hpMonitorFuture == null) { _hpMonitorFuture =
			 * GeneralThreadPool.getInstance() .pcScheduleAtFixedRate(new
			 * L1PcHpr(this), 1000L, INTERVAL_MPHP_MONITOR); }
			 */
			_hpRegenActive = true;
		}
	}

	public void stopHpRegeneration() {
		if (_hpRegenActive) {
			/*
			 * if (_hpMonitorFuture != null) { _hpMonitorFuture.cancel(true);
			 * _hpMonitorFuture = null; }
			 */
			_hpRegen = null;
			_hpRegenActive = false;
		}
	}

	public synchronized void startMpRegeneration() {
		/*
		 * final int INTERVAL = 1000;
		 * 
		 * if (!_mpRegenActive) { _mpRegen = new MpRegeneration(this);
		 * _regenTimer.scheduleAtFixedRate(_mpRegen, INTERVAL, INTERVAL);
		 * _mpRegenActive = true; }
		 */
		if (!_mpRegenActive) {
			if (_mpRegen == null) {
				_mpRegen = new MpRegeneration(this);
			}
			/*
			 * if (_mpMonitorFuture == null) { _mpMonitorFuture =
			 * GeneralThreadPool.getInstance() .pcScheduleAtFixedRate(new
			 * L1PcMpr(this), 1000L, INTERVAL_MPHP_MONITOR); }
			 */
			_mpRegenActive = true;
		}

	}

	/*
	 * public void startMpRegenerationByDoll() { final int INTERVAL_BY_DOLL =
	 * 60000; boolean isExistMprDoll = false; Object[] dollList =
	 * getDollList().values().toArray(); for (Object dollObject : dollList) {
	 * L1DollInstance doll = (L1DollInstance) dollObject; if
	 * (doll.isMpRegeneration()) { isExistMprDoll = true; } } if
	 * (!_mpRegenActiveByDoll && isExistMprDoll) { _mpRegenByDoll = new
	 * MpRegenerationByDoll(this);
	 * _regenTimer.scheduleAtFixedRate(_mpRegenByDoll, INTERVAL_BY_DOLL,
	 * INTERVAL_BY_DOLL); _mpRegenActiveByDoll = true; } }
	 */

	public void stopMpRegeneration() {
		if (_mpRegenActive) {
			/*
			 * if (_mpMonitorFuture != null) { _mpMonitorFuture.cancel(true);
			 * _mpMonitorFuture = null; }
			 */
			_mpRegen = null;
			_mpRegenActive = false;
		}
	}

	public void stopMpRegenerationByDoll() {
		if (_mpRegenActiveByDoll) {
			_mpRegenByDoll.cancel();
			_mpRegenByDoll = null;
			_mpRegenActiveByDoll = false;
		}
	}

	public void startObjectAutoUpdate() {
		removeAllKnownObjects();
		/*
		 * _autoUpdateFuture = GeneralThreadPool.getInstance()
		 * .pcScheduleAtFixedRate(new L1PcAutoUpdate(this), 1000L,
		 * INTERVAL_AUTO_UPDATE);
		 */
	}

	/**
	 * ?????????????????????????????????????????????
	 */
	public void stopEtcMonitor() {
		/*
		 * if (_autoUpdateFuture != null) { _autoUpdateFuture.cancel(true);
		 * _autoUpdateFuture = null; }
		 */
		/*
		 * if (_mpMonitorFuture != null) { _mpMonitorFuture.cancel(true);
		 * _mpMonitorFuture = null; } if (_hpMonitorFuture != null) {
		 * _hpMonitorFuture.cancel(true); _hpMonitorFuture = null; }
		 */
		if (_ghostFuture != null) {
			_ghostFuture.cancel(true);
			_ghostFuture = null;
		}

		if (_hellFuture != null) {
			_hellFuture.cancel(true);
			_hellFuture = null;
		}

	}

	/*
	 * private static final long INTERVAL_AUTO_UPDATE = 300; private
	 * ScheduledFuture<?> _autoUpdateFuture;
	 */

	/*
	 * private static final long INTERVAL_MPHP_MONITOR = 1000; private
	 * ScheduledFuture<?> _mpMonitorFuture; private ScheduledFuture<?>
	 * _hpMonitorFuture;
	 */

	private long _old_exp = 0;

	/**
	 * ??????Lawful
	 * 
	 * @return
	 */
	public long getExpo() {
		return _old_exp;
	}

	public void onChangeExp() {
		final int level = ExpTable.getLevelByExp(getExp());
		final int char_level = getLevel();
		final int gap = level - char_level;
		long oldexp = getExp();
		if (_old_exp != getExp()) {
			oldexp = _old_exp;
			_old_exp = getExp();
		}
		if (gap == 0) {
			// sendPackets(new S_OwnCharStatus(this));
			sendPackets(new S_Exp(this));
			return;
		}

		// ??????????????????????????????
		if (gap > 0) {
			WriteLogTxt.Recording("????????????", "??????" + getName() + "#" + getId()
					+ "#????????????????????????????????????" + oldexp + "?????????" + getLevel() + "???????????????"
					+ getBaseMaxHp() + "???????????????" + getBaseMaxMp());
			levelUp(gap);
			WriteLogTxt.Recording("????????????", "??????" + getName() + "#" + getId()
					+ "#??????????????????????????????" + getExp() + "??????????????????" + getLevel()
					+ "???????????????" + getBaseMaxHp() + "???????????????" + getBaseMaxMp());
		} else if (gap < 0) {
			WriteLogTxt.Recording("????????????", "??????" + getName() + "#" + getId()
					+ "#????????????????????????????????????" + oldexp + "?????????" + getLevel() + "???????????????"
					+ getBaseMaxHp() + "???????????????" + getBaseMaxMp());
			levelDown(gap);
			WriteLogTxt.Recording("????????????", "??????" + getName() + "#" + getId()
					+ "#??????????????????????????????" + getExp() + "??????????????????" + getLevel()
					+ "???????????????" + getBaseMaxHp() + "???????????????" + getBaseMaxMp());
		}
	}

	@Override
	public void onPerceive(final L1PcInstance perceivedFrom) {
		if (isGmInvis() || isGhost() || isInvisble()) {
			return;
		}

		perceivedFrom.addKnownObject(this);
		perceivedFrom.sendPackets(new S_OtherCharPacks(this)); // ????????????????????????
		if (isInParty() && getParty().isMember(perceivedFrom)) { // PT??????????????????HP?????????????????????
			perceivedFrom.sendPackets(new S_HPMeter(this));
		}

		if (isPrivateShop()) {
			perceivedFrom.sendPackets(new S_DoActionShop(getId(),
					ActionCodes.ACTION_Shop, getShopChat()));
		} else if (isFishing()) {
			perceivedFrom.sendPackets(new S_DoActionGFX(getId(), 71));
		}
		if (getFightId() == perceivedFrom.getId()) {
			sendPackets(new S_PacketBox(S_PacketBox.MSG_DUEL, getFightId(),
					getId()));
		}

		if (getPinkSec() > 0) {
			perceivedFrom.sendPackets(new S_PinkName(getId(), getPinkSec()));
		}
		// ???????????? end
		if (isCrown()) { // ??????
			final L1Clan clan = L1World.getInstance().getClan(getClanname());
			if (clan != null) {
				if (getId() == clan.getLeaderId() // ???????????????????????????
						&& clan.getCastleId() != 0) {
					perceivedFrom.sendPackets(new S_CastleMaster(clan
							.getCastleId(), getId()));
				}
			}
		}
	}

	// ????????????????????????????????????????????????????????????
	private void removeOutOfRangeObjects() {
		for (final L1Object known : getKnownObjects()) {
			if (known == null) {
				continue;
			}

			if (Config.PC_RECOGNIZE_RANGE == -1) {
				if (!getLocation().isInScreen(known.getLocation())) { // ?????????
					/*
					 * if (known instanceof L1MonsterInstance) { if
					 * (((L1MonsterInstance) known).getNpcId() == 45291) {
					 * System.out.println("??????45291"); } }
					 */
					removeKnownObject(known);
					sendPackets(new S_RemoveObject(known));
				}
			} else {
				if (getLocation().getTileLineDistance(known.getLocation()) > Config.PC_RECOGNIZE_RANGE) {
					removeKnownObject(known);
					sendPackets(new S_RemoveObject(known));
				}
			}
		}
	}

	// ??????????????????????????????
	public void updateObject() {
		removeOutOfRangeObjects();

		// ??????????????????????????????????????????????????????
		for (final L1Object visible : L1World.getInstance().getVisibleObjects(
				this, Config.PC_RECOGNIZE_RANGE)) {
			if (!knownsObject(visible)) {
				/*
				 * if (visible instanceof L1MonsterInstance) { if
				 * (((L1MonsterInstance) visible).getNpcId() == 45291) { if
				 * (getName().equals("111")) { System.out.println("????????????45291");
				 * } } }
				 */
				visible.onPerceive(this);
			} else {
				if (visible instanceof L1NpcInstance) {
					final L1NpcInstance npc = (L1NpcInstance) visible;
					if (getLocation().isInScreen(npc.getLocation())
							&& npc.getHiddenStatus() != 0) {
						/*
						 * if (visible instanceof L1MonsterInstance) { if
						 * (((L1MonsterInstance) visible).getNpcId() == 45291) {
						 * if (getName().equals("111")) {
						 * System.out.println("????????????45291"); } } }
						 */
						npc.approachPlayer(this);
					}
					// ??????????????????????????????
					if (npc.hasSkillEffect(157)) { // ??????
						npc.broadcastPacket(new S_Poison(npc.getId(), 2));
					} else if (npc.hasSkillEffect(1010)) { // ?????????
						npc.broadcastPacket(new S_Poison(npc.getId(), 2));
					} else if (npc.hasSkillEffect(1011)) { // ?????????
						npc.broadcastPacket(new S_Poison(npc.getId(), 2));
					} else if (npc.hasSkillEffect(1009)
							|| npc.hasSkillEffect(1010)
							|| npc.hasSkillEffect(1011)) {
						npc.broadcastPacket(new S_Poison(npc.getId(), 2));
					} else if (npc.get_poisonStatus6() == 4) { // ??????
						npc.broadcastPacket(new S_Poison(npc.getId(), 2));
					} else if (npc.hasSkillEffect(1006)
							|| npc.hasSkillEffect(1007)
							|| npc.hasSkillEffect(1008)) {
						npc.broadcastPacket(new S_Poison(npc.getId(), 1));
					}
					// ?????????????????????????????? end
				}

				// ??????????????????????????????
				if (visible instanceof L1PcInstance) {
					final L1PcInstance pc = (L1PcInstance) visible;

					if (pc.hasSkillEffect(157)) {
						pc.broadcastPacket(new S_Poison(pc.getId(), 2));
					} else if (pc.hasSkillEffect(1010)) {
						pc.broadcastPacket(new S_Poison(pc.getId(), 2));
					} else if (pc.hasSkillEffect(1011)) {
						pc.broadcastPacket(new S_Poison(pc.getId(), 2));
					} else if (pc.get_poisonStatus6() == 4) {
						pc.broadcastPacket(new S_Poison(pc.getId(), 2));
					} else if (pc.hasSkillEffect(1009)
							|| pc.hasSkillEffect(1010)
							|| pc.hasSkillEffect(1011)) {
						pc.broadcastPacket(new S_Poison(pc.getId(), 2));
					} else if (pc.hasSkillEffect(1006)
							|| pc.hasSkillEffect(1007)
							|| pc.hasSkillEffect(1008)) {
						pc.broadcastPacket(new S_Poison(pc.getId(), 1));
					}
				}
				// ?????????????????????????????? end
			}
			if (visible instanceof L1Character) {
				if (((L1Character) visible).getCurrentHp() > 0) {
					if (isGm() && hasSkillEffect(L1SkillId.GMSTATUS_HPBAR)) {
						sendPackets(new S_HPMeter((L1Character) visible));
					}
				}
			}
		}
	}

	private void sendVisualEffect() {
		int poisonId = 0;
		if (getPoison() != null) { // ?????????
			poisonId = getPoison().getEffectId();
		}
		if (getParalysis() != null) { // ????????????
			// ??????????????????????????????????????????????????????poisonId???????????????
			poisonId = getParalysis().getEffectId();
		}
		if (poisonId != 0) { // ??????if?????????????????????????????????
			sendPackets(new S_Poison(getId(), poisonId));
			broadcastPacket(new S_Poison(getId(), poisonId));
		}
	}

	public void sendVisualEffectAtLogin() {
		for (final L1Clan clan : L1World.getInstance().getAllClans()) {
			sendPackets(new S_Emblem(clan.getClanId()));
		}

		if (getClanid() != 0) { // ???????????????
			final L1Clan clan = L1World.getInstance().getClan(getClanname());
			if (clan != null) {
				if (isCrown() && getId() == clan.getLeaderId() && // ?????????????????????????????????????????????????????????????????????????????????
						clan.getCastleId() != 0) {
					sendPackets(new S_CastleMaster(clan.getCastleId(), getId()));
				}
			}
		}

		sendVisualEffect();
	}

	public void sendVisualEffectAtTeleport() {
		if (isDrink()) { // liquor??????????????????
			sendPackets(new S_Liquor(getId()));
		}

		sendVisualEffect();
	}

    private Boolean _skill187;//???????????????false??????-ture??????

    public boolean isskill187() {
        return this._skill187;
    }

    public void setskill187(boolean setskill187) {//?????????????????????false??????-ture??????
        this._skill187 = setskill187;
        if (setskill187 && isSkillMastery(187)) AutoMagic.automagic(this, 187);
    }

    private Boolean _skill132;//???????????????false??????-ture??????

    public boolean isskill132() {
        return this._skill132;
    }

    public void setskill132(boolean setskill132) {
        this._skill132 = setskill132;
        if (setskill132 && isSkillMastery(132)) AutoMagic.automagic(this, 132);
    }

    private Boolean _skill46;//???????????????false??????-ture??????

    public boolean isskill46() {
        return this._skill46;
    }

    public void setskill46(boolean setskill46) {
        this._skill46 = setskill46;
        if (setskill46 && isSkillMastery(46)) AutoMagic.automagic(this, 46);
    }


	public L1PcInstance() {
		_speed = new AcceleratorChecker(this);
		_accessLevel = 0;
		_currentWeapon = 0;
		_inventory = new L1PcInventory(this);
		// _dwarf = new L1DwarfInventory(this);
		// _tradewindow = new L1Inventory();
		_quest = new L1Quest(this);
		_equipSlot = new L1EquipmentSlot(this); //
		_uplevelList = new HashMap<Integer, Integer>();
		_gamSpList = new L1GamSpList(this); // ??????
		_action = new L1PCAction(this);
		_tradewindow = new L1Inventory();
		_blessEnchant = new L1PcBlessEnchant(this);
        this._skill46 = false;//???????????????????????????
        this._skill132 = false;//???????????????????????????
        this._skill187 = false;//???????????????????????????
	}

	public L1PcBlessEnchant getBlessEnchant() {
		return _blessEnchant;
	}

	public L1Inventory getTradeWindowInventory() {
		return _tradewindow;
	}

	private final List<Integer> skillList = new ArrayList<Integer>();

	public void setSkillMastery(final int skillid) {
		if (!skillList.contains(skillid)) {
			skillList.add(skillid);
		}
	}

	public void removeSkillMastery(final int skillid) {
		if (skillList.contains(skillid)) {
			skillList.remove((Object) skillid);
		}
	}

	public boolean isSkillMastery(final int skillid) {
		return skillList.contains(skillid);
	}

	public void clearSkillMastery() {
		skillList.clear();
	}

	@Override
	public void setCurrentHp(final int i) {
		if (getCurrentHp() == i) {
			return;
		}
		int currentHp = i;
		if (currentHp >= getMaxHp()) {
			currentHp = getMaxHp();
		}
		setCurrentHpDirect(currentHp);
		sendPackets(new S_HPUpdate(currentHp, getMaxHp()));
		if (isInParty()) { // ??????????????????
			getParty().updateMiniHP(this);
		}
	}

	@Override
	public void setCurrentMp(final int i) {
		if (getCurrentMp() == i) {
			return;
		}
		int currentMp = i;
		if (currentMp >= getMaxMp() || isGm()) {
			currentMp = getMaxMp();
		}
		setCurrentMpDirect(currentMp);
		sendPackets(new S_MPUpdate(currentMp, getMaxMp()));
	}


	@Override
	public L1PcInventory getInventory() {
		return _inventory;
	}
	/*
	 * public L1DwarfInventory getDwarfInventory() { return _dwarf; }
	 */

	/*
	 * public L1Inventory getTradeWindowInventory() { return _tradewindow; }
	 */

	public int getCurrentWeapon() {
		return _currentWeapon;
	}

	public void setCurrentWeapon(final int i) {
		_currentWeapon = i;
	}

	public int getType() {
		return _type;
	}

	/**
	 *  0:?????? 1:?????? 2:?????? 3:?????? 4:?????? 5:?????? 6:??????
	 * 
	 * QQ???1043567675
	 * by????????????
	 * 2020???5???6???
	 * ??????11:07:44
	 */
	public void setType(final int i) {
		_type = i;
	}

	public short getAccessLevel() {
		return _accessLevel;
	}

	public void setAccessLevel(final short i) {
		_accessLevel = i;
	}

	public void addAccessLevel(final int i) {
		_accessLevel += i;
	}

	public int getClassId() {
		return _classId;
	}

	public void setClassId(final int i) {
		_classId = i;
		_classFeature = L1ClassFeature.newClassFeature(i);
	}

	private L1ClassFeature _classFeature = null;

	public L1ClassFeature getClassFeature() {
		return _classFeature;
	}

	@Override
	public synchronized long getExp() {
		return _exp;
	}

	@Override
	public synchronized void setExp(final long i) {
		_exp = i;
	}

	private int _PKcount; // ??? PK????????????

	public int get_PKcount() {
		return _PKcount;
	}

	public void set_PKcount(final int i) {
		_PKcount = i;
	}

	public void add_PKcount(final int i) {
		_PKcount += i;
	}

	private int _clanid; // ??? ???????????????

	public int getClanid() {
		return _clanid;
	}

	public void setClanid(final int i) {
		_clanid = i;
	}

	private String clanname; // ??? ????????????

	public String getClanname() {
		return clanname;
	}

	public void setClanname(final String s) {
		clanname = s;
	}

	// ???????????????????????????????????????????????????????????????
	public L1Clan getClan() {
		return L1World.getInstance().getClan(getClanname());
	}

	public L1Pc getPc() {
		return L1World.getInstance().getPc(getAccountName());
	}

	private byte _sex; // ??? ??????

	public byte get_sex() {
		return _sex;
	}

	public void set_sex(final int i) {
		_sex = (byte) i;
	}

	public boolean isGm() {
		return _gm;
	}

	public void setGm(final boolean flag) {
		_gm = flag;
	}

	public boolean isMonitor() {
		return _monitor;
	}

	public void setMonitor(final boolean flag) {
		_monitor = flag;
	}

	private L1PcInstance getStat() {
		return null;
	}

	public void reduceCurrentHp(final double d, final L1Character l1character) {
		getStat().reduceCurrentHp(d, l1character);
	}

	/**
	 * ??????????????????????????????????????????????????????????????????????????????
	 * 
	 * @param ????????????????????????????????????
	 */
	private void notifyPlayersLogout(final Collection<L1PcInstance> collection) {
		for (final L1PcInstance player : collection) {
			if (player.knownsObject(this)) {
				player.removeKnownObject(this);
				player.sendPackets(new S_RemoveObject(this));
			}
		}
	}

	public void logout() {
		final L1World world = L1World.getInstance();
		if (getClanid() != 0) // ???????????????
		{
			final L1Clan clan = world.getClan(getClanname());
			if (clan != null) {
				if (clan.getWarehouseUsingChar() == getId()) // ???????????????????????????????????????
				{
					clan.setWarehouseUsingChar(0); // ????????????????????????????????????
				}
			}
		}
		if (this.isPrivateShop()
				&& ((this.getMapId() == 340 || this.getMapId() == 350
						|| this.getMapId() == 360 || this.getMapId() == 370) || this
						.getInventory().checkEquipped(25069))) {// ????????????

		}else if(Config.dummyFunction){//????????????????????????????????????
			if (Config.dummyFunction && getZoneType() == 1) {
				L1WilliamJiaRen.getInstance().addlogout(this.getAccountName(), this);
			}
		}else {
			notifyPlayersLogout(getKnownPlayers());
			world.removeWorldObject(this);
			world.removeVisibleObject(this);
			notifyPlayersLogout(world.getRecognizePlayer(this));
			_inventory.clearItems();
			setDead(true);
		}
		_tempObjects.clear();
		// _dwarf.clearItems();
		removeAllKnownObjects();
		stopHpRegeneration();
		stopMpRegeneration();
		setNetConnection(null);
		// ??????????????????????????????????????????????????????????????????????????????????????????????????????
		_spawnBossList.clear();
		_blessEnchant.clear();
		// setPacketOutput(null);
	}

	public void clearTuoJiShop() {
		final L1World world = L1World.getInstance();
		notifyPlayersLogout(getKnownPlayers());
		world.removeWorldObject(this);
		world.removeVisibleObject(this);
		notifyPlayersLogout(world.getRecognizePlayer(this));
		removeAllKnownObjects();
		_inventory.clearItems();
		setDead(true);
	}

	public LineageClient getNetConnection() {
		return _netConnection;
	}

	public void setNetConnection(final LineageClient _client) {
		_netConnection = _client;
	}

	public boolean isInParty() {
		return getParty() != null;
	}

	public L1Party getParty() {
		return _party;
	}

	public void setParty(final L1Party p) {
		_party = p;
	}

	public int getPartyID() {
		return _partyID;
	}

	public void setPartyID(final int partyID) {
		_partyID = partyID;
	}

	public int getTradeID() {
		return _tradeID;
	}

	public void setTradeID(final int tradeID) {
		_tradeID = tradeID;
	}

	public void setTradeOk(final boolean tradeOk) {
		_tradeOk = tradeOk;
	}

	public boolean getTradeOk() {
		return _tradeOk;
	}

	public int getTempID() {
		return _tempID;
	}

	public void setTempID(final int tempID) {
		_tempID = tempID;
	}

	public int getTempCount() {
		return _tempCount;
	}

	public void setTempCount(final int tempCount) {
		_tempCount = tempCount;
	}

	public boolean isTeleport() {
		return _isTeleport;
	}

	public void setTeleport(final boolean flag) {
		_isTeleport = flag;
	}

	public boolean isDrink() {
		return _isDrink;
	}

	public void setDrink(final boolean flag) {
		_isDrink = flag;
	}

	public boolean isGres() {
		return _isGres;
	}

	public void setGres(final boolean flag) {
		_isGres = flag;
	}

	/*
	 * public boolean isPinkName() { return _isPinkName; }
	 * 
	 * public void setPinkName(boolean flag) { _isPinkName = flag; }
	 */

	private final ArrayList<L1PrivateShopSellList> _sellList = new ArrayList<L1PrivateShopSellList>();

	public ArrayList<L1PrivateShopSellList> getSellList() {
		return _sellList;
	}

	private final ArrayList<L1PrivateShopBuyList> _buyList = new ArrayList<L1PrivateShopBuyList>();

	public ArrayList<L1PrivateShopBuyList> getBuyList() {
		return _buyList;
	}

	private String[] _shopChat;

	public void setShopChat(final String[] chat) {
		_shopChat = chat;
	}

	public String[] getShopChat() {
		return _shopChat;
	}

	private boolean _isPrivateShop = false;

	public boolean isPrivateShop() {
		return _isPrivateShop;
	}

	public void setPrivateShop(final boolean flag) {
		_isPrivateShop = flag;
	}

	private boolean _isTradingInPrivateShop = false;

	public boolean isTradingInPrivateShop() {
		return _isTradingInPrivateShop;
	}

	public void setTradingInPrivateShop(final boolean flag) {
		_isTradingInPrivateShop = flag;
	}

	private int _partnersPrivateShopItemCount = 0; // ??????????????????????????????????????????

	public int getPartnersPrivateShopItemCount() {
		return _partnersPrivateShopItemCount;
	}

	public void setPartnersPrivateShopItemCount(final int i) {
		_partnersPrivateShopItemCount = i;
	}

	/*
	 * private OutputStream _out;
	 * 
	 * public void setPacketOutput(OutputStream out) { _out = out; }
	 */

	public void sendPackets(final ServerBasePacket serverbasepacket) {
		if (_netConnection == null) {
			return;
		}
		try {
			_netConnection.sendPacket(serverbasepacket);
		} catch (final Exception e) {
		}
	}

	public void sendPacketsAll(final ServerBasePacket serverbasepacket) {
		if (_netConnection == null) {
			return;
		}
		try {
			_netConnection.sendPacket(serverbasepacket);
			for (final L1PcInstance pc : L1World.getInstance()
					.getRecognizePlayer(this)) {
				pc.sendPackets(serverbasepacket);
			}
		} catch (final Exception e) {
		}
	}

	@Override
	public void onAction(final L1PcInstance attacker) {
		// XXX:NullPointerException?????????onAction??????????????????L1Character?????????????????????
		if (attacker == null) {
			return;
		}
		// ????????????????????????
		if (isTeleport()) {
			return;
		}
		// ????????????????????????????????????????????????????????????????????????
		if (getZoneType() == 1 || attacker.getZoneType() == 1) {
			// ???????????????????????????
			final L1Attack attack_mortion = new L1Attack(attacker, this);
			attack_mortion.action();
			return;
		}

		if (checkNonPvP(this, attacker) == true) {
			return;
		}

		if (getCurrentHp() > 0 && !isDead()) {
			attacker.delInvis();

			final boolean isCounterBarrier = false;
			final L1Attack attack = new L1Attack(attacker, this);
			if (attack.calcHit()) {
				/*
				 * ??????if (hasSkillEffect(L1SkillId.COUNTER_BARRIER)) { L1Magic
				 * magic = new L1Magic(this, attacker); boolean isProbability =
				 * magic .calcProbabilityMagic(L1SkillId.COUNTER_BARRIER);
				 * boolean isShortDistance = attack.isShortDistance(); if
				 * (isProbability && isShortDistance) { isCounterBarrier = true;
				 * } }??????
				 */
				if (!isCounterBarrier) {
					attacker.setPetTarget(this);

					attack.calcDamage();
					attack.calcStaffOfMana();
					attack.addPcPoisonAttack(attacker, this);
				}
			}
			if (isCounterBarrier) {
				attack.actionCounterBarrier();
				attack.commitCounterBarrier();
			} else {
				attack.action();
				attack.commit();
			}
		}
	}

	public boolean checkNonPvP(final L1PcInstance pc, final L1Character target) {
		L1PcInstance targetpc = null;
		if (target instanceof L1PcInstance) {
			targetpc = (L1PcInstance) target;
		} else if (target instanceof L1PetInstance) {
			targetpc = (L1PcInstance) ((L1PetInstance) target).getMaster();
		} else if (target instanceof L1SummonInstance) {
			targetpc = (L1PcInstance) ((L1SummonInstance) target).getMaster();
		}
		if (targetpc == null) {
			return false; // ?????????PC??????????????????????????????
		}
		if (!Config.ALT_NONPVP) { // Non-PvP??????
			if (getMap().isCombatZone(getLocation())) {
				return false;
			}

			// ???????????????????????????
			for (final L1War war : L1World.getInstance().getWarList()) {
				if (pc.getClanid() != 0 && targetpc.getClanid() != 0) { // ????????????????????????
					final boolean same_war = war.CheckClanInSameWar(
							pc.getClanname(), targetpc.getClanname());
					if (same_war == true) { // ????????????????????????
						return false;
					}
				}
			}
			// Non-PvP???????????????????????????????????????????????????
			if (target instanceof L1PcInstance) {
				final L1PcInstance targetPc = (L1PcInstance) target;
				if (isInWarAreaAndWarTime(pc.getX(), pc.getY(), pc.getMapId(),
						targetPc)) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	private boolean isInWarAreaAndWarTime(final int x, final int y,
			final int mapId, final L1PcInstance target) {
		// pc???target????????????????????????????????????
		final L1Location deathloc = new L1Location(x, y, mapId);
		final int castleId = L1CastleLocation.getCastleIdByArea(deathloc);
		final int targetCastleId = L1CastleLocation.getCastleIdByArea(target);
		if (castleId != 0 && targetCastleId != 0 && castleId == targetCastleId) {
			if (WarTimeController.getInstance().isNowWar(castleId)) {
				return true;
			}
		}
		return false;
	}

	public void setPetTarget(final L1Character target) {
		final Object[] petList = getPetList().values().toArray();
		for (final Object pet : petList) {
			if (pet instanceof L1PetInstance) {
				final L1PetInstance pets = (L1PetInstance) pet;
				pets.setMasterTarget(target);
			} else if (pet instanceof L1SummonInstance) {
				final L1SummonInstance summon = (L1SummonInstance) pet;
				summon.setMasterTarget(target);
			}
		}
	}

	public void delInvis() {
		// ??????????????????????????????????????????
		if (hasSkillEffect(L1SkillId.INVISIBILITY)) { // ????????????????????????
			killSkillEffectTimer(L1SkillId.INVISIBILITY);
			sendPackets(new S_Invis(getId(), 0));
			broadcastPacket(new S_OtherCharPacks(this));
		}
		if (hasSkillEffect(L1SkillId.BLIND_HIDING)) { // ??????????????? ??????????????????
			killSkillEffectTimer(L1SkillId.BLIND_HIDING);
			sendPackets(new S_Invis(getId(), 0));
			broadcastPacket(new S_OtherCharPacks(this));
		}
	}

	public void delBlindHiding() {
		// ????????????????????????????????????
		killSkillEffectTimer(L1SkillId.BLIND_HIDING);
		sendPackets(new S_Invis(getId(), 0));
		broadcastPacket(new S_OtherCharPacks(this));
	}

	// ???????????????????????????????????????????????? (???????????????????????????????????????) attr:0.???????????????,1.?????????,2.?????????,3.?????????,4.?????????
	public void receiveDamage(final L1Character attacker, int damage,
			final int attr) {
		final Random random = new Random();
		final int player_mr = getMr();
		final int rnd = random.nextInt(100) + 1;
		if (player_mr >= rnd) {
			damage /= 2;
		}
		receiveDamage(attacker, damage ,false);
	}

	public void receiveManaDamage(final L1Character attacker, final int mpDamage) { // ???????????????????????????????????????????????????
		if (mpDamage > 0 && !isDead()) {
			delInvis();
			// System.out.println("???????????????:"+attacker.getName());
			L1PinkName.onAction(this, attacker);

			int newMp = getCurrentMp() - mpDamage;
			if (newMp > getMaxMp()) {
				newMp = getMaxMp();
			}

			if (newMp <= 0) {
				newMp = 0;
			}
			setCurrentMp(newMp);
		}
	}

	public void receiveDamage(final L1Character attacker, final int damage, final boolean isCounterBarrier) { // ???????????????????????????????????????????????????
		if (getCurrentHp() > 0 && !isDead()) {
			if (attacker != this && !knownsObject(attacker)) {
				attacker.onPerceive(this);
			}

			if (damage > 0) {
				delInvis();
				// System.out.println("?????????:"+attacker.getName());
				L1PinkName.onAction(this, attacker);
				removeSkillEffect(L1SkillId.FOG_OF_SLEEPING);
				if (attacker instanceof L1PcInstance) {
					/*
					 * if (attacker.isVdmg()) { L1PcInstance player =
					 * (L1PcInstance) attacker; String msg = "??????->" + damage;
					 * S_ChatPacket s_chatpacket = new S_ChatPacket(this, msg,
					 * Opcodes.S_OPCODE_NORMALCHAT);
					 * player.sendPackets(s_chatpacket); }
					 */
					attacker.setAttack(true);
					attacker.setAttacksec(10);
				}
				setAttack(false);
				setAttacksec(10);
				if (attacker.isPVP()) {
					if (attacker instanceof L1PcInstance) {
						final L1PcInstance fightPc = (L1PcInstance) attacker;
						if (fightPc.getFightId() != getId()) {
							fightPc.setFightId(getId());
							fightPc.sendPackets(new S_PacketBox(
									S_PacketBox.MSG_DUEL, fightPc.getFightId(),
									fightPc.getId()));
						}
					}
				}
				L1PcInstance attackPc = null;
				L1NpcInstance attackNpc = null;
				if (attacker instanceof L1PcInstance) {
					attackPc = (L1PcInstance) attacker;// ????????????PC

				} else if (attacker instanceof L1NpcInstance) {
					attackNpc = (L1NpcInstance) attacker;// ????????????NPC
				}
				if (!isCounterBarrier) {// false:????????????
					// ????????????(??????????????????)
					if (this.hasSkillEffect(L1SkillId.MORTAL_BODY)) {
						//System.out.println("?????????????????????");
						if (this.getId() != attacker.getId()) {
							final int rnd = _random.nextInt(100) + 1;
							if ((damage > 0) && (rnd <= 18)) {// 2011-11-26 0-15
								final int dmg = attacker.getLevel() >> 1;// ??????????????????????????????????????????.hjx1000
								// SRC DMG = 50
								if (attackPc != null) {
									attackPc.sendPacketsAll(new S_DoActionGFX(
											attackPc.getId(),
											ActionCodes.ACTION_Damage));
									attackPc.receiveDamage(this, dmg,
											true);

								} else if (attackNpc != null) {
									attackNpc
											.broadcastPacket(new S_DoActionGFX(
													attackNpc.getId(),
													ActionCodes.ACTION_Damage));
									attackNpc.receiveDamage(this, dmg);
								}
							}
						}
					}
				}
			}

			int newHp = getCurrentHp() - damage;
			if (newHp > getMaxHp()) {
				newHp = getMaxHp();
			}
			if (newHp <= 0) {
				if (isGm()) {
					setCurrentHp(getMaxHp());
				} else {
					// ????????????????????????????????????
					if (get_poisonStatus2() == 4) {
						final L1Poison2 poison = get_poison2();
						if (poison != null) {
							poison.CurePoison(this);
							del_poison2();
						}
					}
					if (get_poisonStatus3() == 4) {
						final L1Poison3 poison = get_poison3();
						if (poison != null) {
							poison.CurePoison(this);
							del_poison3();
						}
					}
					if (get_poisonStatus4() == 4) {
						final L1Poison4 poison = get_poison4();
						if (poison != null) {
							poison.CurePoison(this);
							del_poison4();
						}
					}
					if (get_poisonStatus6() == 4) {
						final L1Poison6 poison = get_poison6();
						if (poison != null) {
							poison.CurePoison(this);
							del_poison6();
						}
					}
					// ???????????????????????????????????? end
					death(attacker);
				}
			}
			if (newHp > 0) {
				setCurrentHp(newHp);
			}
		} else if (!isDead()) { // ????????????
			System.out.println("??????????????????????????????????????????");
			death(attacker);
		}
	}

	public void death(final L1Character lastAttacker) {
		synchronized (this) {
			if (this.isActived()) { // ????????????
				if (this.getInventory().consumeItem(40308, 500)) {
					this.setCurrentHp(this.getMaxHp()); // ????????????
					this.sendPackets(new S_SystemMessage("????????????.????????????500."));
				} else {
					this.sendPackets(new S_SystemMessage(
							"????????????500??????.????????????????????????????????????..."));
					this.setActived(false);
					final L1Location newLocation = new L1Location(33437, 32812,
							4).randomLocation(10, false);
					L1Teleport.teleport(this, newLocation.getX(),
							newLocation.getY(), (short) newLocation.getMapId(),
							5, true);
				}
				return;
			}
			if (isDead()) {
				return;
			}
			setDead(true);
			setDeathProcessing(true);
			setStatus(ActionCodes.ACTION_Die);
		}
		GeneralThreadPool.getInstance().execute(new Death(lastAttacker));

	}

	private boolean _deathProcessing;

	/**
	 * ???????????????
	 * 
	 * @param deathProcessing
	 */
	public void setDeathProcessing(final boolean deathProcessing) {
		this._deathProcessing = deathProcessing;
	}

	/**
	 * ???????????????
	 * 
	 * @return
	 */
	public boolean isDeathProcessing() {
		return this._deathProcessing;
	}

	private class Death implements Runnable {
		L1Character _lastAttacker;

		Death(final L1Character cha) {
			_lastAttacker = cha;
		}

		public void run() {
			final L1Character lastAttacker = _lastAttacker;
			final int deathMapId = L1PcInstance.this.getMapId();
			final int deathLocX = L1PcInstance.this.getX();
			final int deathLocY = L1PcInstance.this.getY();
			_lastAttacker = null;

			setCurrentHp(0);
			setGresValid(false); // EXP?????????????????????G-RES??????
			add_Deathcount(1);

			// ??????????????????????????????
			Object[] petList = getPetList().values().toArray();
			for (Object petObject : petList) {
				if (petObject instanceof L1BabyInstance) { // ?????????
					L1BabyInstance baby = (L1BabyInstance) petObject;
					baby.Death(null);
					getPetList().remove(baby.getId());
				}
			}
			if (isTeleport()) { // ?????????????????????????????????????????????
				try {
					Thread.sleep(300);
				} catch (final Exception e) {
				}
			}

			stopHpRegeneration();
			stopMpRegeneration();

			final int targetobjid = getId();
			getMap().setPassable(getLocation(), true);

			// ?????????????????????????????????
			// ?????????????????????????????????????????????????????????????????????????????????????????????????????????
			int tempchargfx = 0;
			if (hasSkillEffect(L1SkillId.SHAPE_CHANGE)) {
				tempchargfx = getTempCharGfx();
				setTempCharGfxAtDead(tempchargfx);
			} else {
				setTempCharGfxAtDead(getClassId());
			}
			// ???????????????????????????????????????????????????????????????
			final L1SkillUse l1skilluse = new L1SkillUse();
			l1skilluse.handleCommands(L1PcInstance.this,
					L1SkillId.CANCELLATION, getId(), getX(), getY(), null, 0,
					L1SkillUse.TYPE_LOGIN);

			if (tempchargfx > 0) {
				sendPacketsAll(new S_ChangeShape(getId(), tempchargfx));
			}

			sendPackets(new S_DoActionGFX(targetobjid, ActionCodes.ACTION_Die));
			broadcastPacket(new S_DoActionGFX(targetobjid,
					ActionCodes.ACTION_Die));

			setDeathProcessing(false);

			// ?????????????????????????????????????????????????????????????????????????????????
			L1PcInstance player = null;
			if (lastAttacker instanceof L1PcInstance) {
				player = (L1PcInstance) lastAttacker;
			} else if (lastAttacker instanceof L1PetInstance) {
				player = (L1PcInstance) ((L1PetInstance) lastAttacker)
						.getMaster();
			} else if (lastAttacker instanceof L1SummonInstance) {
				player = (L1PcInstance) ((L1SummonInstance) lastAttacker)
						.getMaster();
			}
			if (player != null) {
				player.add_PKcount(1);
				if (player.getClanid() != 0) {
					L1World.getInstance().broadcastPacketToAll(
							new S_ServerMessage(4533, L1PcInstance.this
									.getName(), player.getClanname(), player
									.getName()));
				} else {
					L1World.getInstance().broadcastPacketToAll(
							new S_ServerMessage(4534, L1PcInstance.this
									.getName(), player.getName()));
				}
				if (L1PcInstance.this.getMapId() == Config.HUODONGMAPID) {
					final L1ItemInstance deathItem = player.getInventory()
							.storeItem(10043, 1);
					if (deathItem != null) {
						player.sendPackets(new S_SystemMessage(String.format(
								"???????????????????????????%s", deathItem.getItem().getName())));
					}
				}
				if (player.isKOGifd()) {
					player.sendPackets(new S_SkillSound(player.getId(), 12111));
				}
			}

			if (lastAttacker != L1PcInstance.this) {
				// ???????????????????????????????????????????????????????????????????????????????????????
				// ???????????????or?????????????????????????????????????????????
				if (L1PcInstance.this.getZoneType() != 0) {
					if (player != null) {
						// ???????????????????????????????????????????????????
						if (!isInWarAreaAndWarTime(deathLocX, deathLocY,
								deathMapId, player)) {
							return;
						}
					}
				}

				final boolean sim_ret = simWarResult(lastAttacker); // ?????????
				if (sim_ret == true) { // ???????????????????????????????????????
					return;
				}
			}

			if (!getMap().isEnabledDeathPenalty()) {
				return;
			}

			String attackName = "";
			boolean isDeathEXP = true;
			if (lastAttacker instanceof L1MonsterInstance) {
				attackName = "??????";
				if (getInventory().consumeItem(99998, 1)) {
					isDeathEXP = false;
					sendPackets(new S_SystemMessage("????????????????????????????????????????????????"));
				}
			} else if (lastAttacker instanceof L1PcInstance) {
				attackName = "??????";
				if (getInventory().consumeItem(10024, 1)) {
					isDeathEXP = false;
					sendPackets(new S_SystemMessage("????????????????????????????????????????????????"));
					// final L1PcInstance tagerAttackPc =
					// (L1PcInstance)lastAttacker;
					// tagerAttackPc.getInventory().storeItem(40308, 100000);
					// tagerAttackPc.sendPackets(new S_SystemMessage("??????" +
					// L1PcInstance.this.getName() +
					// "??????????????????????????????(PK)????????????10??????????????????"));
				} else {
					final L1ItemInstance vipring = L1PcInstance.this
							.getInventory().findEquipped(70030);
					if (vipring != null) {
						isDeathEXP = false;
						L1PcInstance.this.getInventory().removeItem(vipring);
						L1PcInstance.this.sendPackets(new S_SystemMessage(
								"????????????????????????????????????????????????"));
						if (player != null) {
							int count = 2;
							player.getInventory().storeItem(44070, count);
							L1World.getInstance().broadcastServerMessage(
									String.format("\\F4??????(:" + getName()
											+ ")???????????????[" + player.getName()
											+ "]?????????????????????"+count+"??????"));
							// player.sendPackets(new S_SystemMessage("??????"
							// + L1PcInstance.this.getName()
							// + "??????????????????????????????????????????10??????????????????"));
						}
					}
				}
			}
			if (isDeathEXP) {
				deathPenalty(); // EXP?????????
				setGresValid(true); // EXP??????????????????G-RES??????
				if (getExpRes() == 0) {
					setExpRes(1);
				}
			}
			if (lastAttacker != null) {
				WriteLogTxt.Recording("?????????????????????", "?????? " + getName() + " ??? "
						+ attackName + lastAttacker.getName() + " ????????????");
			} else {
				WriteLogTxt.Recording("?????????????????????", "?????? " + getName()
						+ " ??????????????????????????????????????????");
			}
			// System.out.println("????????????"+lastAttacker.getName());

			setLastPk(null);

			// ?????????????????????????????????DROP
			// ??????????????????32000?????????0%?????????-1000??????0.4%
			// ?????????????????????0??????????????????-1000??????0.8%
			// ??????????????????-32000???????????????51.2%???DROP???
			if (getLawful() < 32767) {
				int lostRate = _random.nextInt(70) + 1;
				int lostRate1 = _random.nextInt(60) + 1;
				int lostRate2 = _random.nextInt(50) + 1;
				int lostRate3 = _random.nextInt(40) + 1;
				int lostRate4 = _random.nextInt(20) + 1;
				int lostRate5 = _random.nextInt(15) + 1;

				int count = 0;
				int lawful = L1PcInstance.this.getLawful();
				if (lawful <= -32768 + lostRate) {// ??????-30000??????1~5???
					count = _random.nextInt(6) + 1;

				} else if (lawful > -32768 && lawful <= -30000 + lostRate1) {// ??????-30000??????1~5???
					count = _random.nextInt(5) + 1;

				} else if (lawful > -30000 && lawful <= -20000 + lostRate2) {// ??????-20000??????1~4???
					count = _random.nextInt(4) + 1;

				} else if (lawful > -20000 && lawful <= -10000 + lostRate3) {// ??????-10000??????1~3???
					count = _random.nextInt(3) + 1;

				} else if (lawful > -10000 && lawful <= -0 + lostRate4) {// ??????500??????1???
					count = _random.nextInt(1) + 1;

				} else if (lawful > 1 && lawful <= 30000 + lostRate5) {// ??????0??????1???
					count = _random.nextInt(1) + 1;
				}

				if (count > 0) {
					L1PcInstance.this.caoPenaltyResult(count);
				}
			}

			final boolean castle_ret = castleWarResult(deathLocX, deathLocY,
					deathMapId); // ?????????
			if (castle_ret == true) { // ??????????????????????????????
				if (player != null) {
					WriteLogTxt.Recording(
							"????????????PK????????????",
							"?????????????????? " + getName() + " ???????????? " + getLawful()
									+ " OBJID#" + getId() + "# ???X:" + getX()
									+ " Y:" + getY() + " MAPID" + getMapId()
									+ "# ?????????" + player.getName() + "OBJID#"
									+ player.getId() + "# ???X:" + player.getX()
									+ " Y:" + player.getY() + " MAPID"
									+ player.getMapId() + "#????????????????????????????????????"
									+ player.getLawful() + "????????????????????????"
									+ player.getLawful());
				}
				return;
			}
			if (player != null) {
				if (getLawful() >= 0 && isPinkName() == false) {
					if (player.getLawful() < 30000) {
						player.setLastPk();
					}
					int lawful;

					final int oldlawful = player.getLawful();

					if (player.getLevel() < 50) {
						lawful = -1
								* (int) ((Math.pow(player.getLevel(), 2) * 4));
					} else {
						lawful = -1
								* (int) ((Math.pow(player.getLevel(), 3) * 0.08));
					}
					if ((player.getLawful() - 1000) < lawful) {
						lawful = player.getLawful() - 1000;
					}

					if (lawful <= -32768) {
						lawful = -32768;
					}
					player.setLawful(lawful);

					final S_Lawful s_lawful = new S_Lawful(player.getId(),
							player.getLawful());
					player.sendPackets(s_lawful);
					player.broadcastPacket(s_lawful);
					WriteLogTxt.Recording(
							"??????PK???????????????",
							"?????????????????? " + getName() + " ???????????? " + getLawful()
									+ " OBJID#" + getId() + "# ???X:" + getX()
									+ " Y:" + getY() + " MAPID" + getMapId()
									+ "# ?????????" + player.getName() + "OBJID#"
									+ player.getId() + "# ???X:" + player.getX()
									+ " Y:" + player.getY() + " MAPID"
									+ player.getMapId() + "#????????????????????????????????????"
									+ oldlawful + "????????????????????????"
									+ player.getLawful());
				} else {
					setPinkSec(0);
					L1PinkName.stopPinkName(L1PcInstance.this);
				}
			}

		}
	}

	private void caoPenaltyResult(final long count) {
		for (int i = 0; i < count; i++) {
			final L1ItemInstance item = getInventory().CaoPenalty();
			if (item != null) {
				if (!item.getItem().isTradable()) {
					continue;
				}
				if (item.getItem().getType2() != 0) {
					if (L1PcInstance.this.getInventory().checkItem(10048)) {
						continue;
					}
				}
				if (item.isSeal()) {
					WriteLogTxt.Recording("??????????????????", "?????? " + getName()
							+ " ???????????? " + getLawful() + " OBJID#" + getId()
							+ "# ???X:" + getX() + " Y:" + getY() + " MAPID"
							+ getMapId() + "# ??????????????????" + item.getLogViewName()
							+ "ITEMOBJID#" + item.getId() + "#");
					sendPackets(new S_ServerMessage(638, item.getLogViewName())); // %0?????????????????????
					getInventory().removeItem(item);
				} else {
					final String mapName = MapsTable.getInstance().getMapName(
							this.getMapId(), this.getX(), this.getY());
					L1World.getInstance().broadcastPacketToAll(
							new S_ServerMessage(4538, this.getName(), mapName,
									this.getX() + "," + this.getY(), item
											.getLogViewName()));
					item.setKillDeathName(this.getName());
					getInventory().tradeItem(
							item,
							item.isStackable() ? item.getCount() : 1,
							L1World.getInstance().getInventory(getX(), getY(),
									getMapId()));
					sendPackets(new S_ServerMessage(638, item.getLogViewName())); // %0?????????????????????
					WriteLogTxt.Recording("??????????????????", "?????? " + getName()
							+ " ???????????? " + getLawful() + " OBJID#" + getId()
							+ "# ???X:" + getX() + " Y:" + getY() + " MAPID"
							+ getMapId() + "# ????????????" + item.getLogViewName()
							+ "ITEMOBJID#" + item.getId() + "#");
				}

			}
		}
	}

	public boolean castleWarResult(final int x, final int y, final int mapId) {
		if (getClanid() != 0 && isCrown()) { // ???????????????????????????????????????
			final L1Clan clan = L1World.getInstance().getClan(getClanname());
			// ???????????????????????????
			for (final L1War war : L1World.getInstance().getWarList()) {
				final int warType = war.GetWarType();
				final boolean isInWar = war.CheckClanInWar(getClanname());
				final boolean isAttackClan = war.CheckAttackClan(getClanname());
				if (getId() == clan.getLeaderId() && // ????????????????????????????????????
						warType == 1 && isInWar && isAttackClan) {
					final String enemyClanName = war
							.GetEnemyClanName(getClanname());
					if (enemyClanName != null) {
						war.CeaseWar(getClanname(), enemyClanName); // ??????
					}
					break;
				}
			}
		}

		int castleId = 0;
		boolean isNowWar = false;
		castleId = L1CastleLocation.getCastleIdByArea(new L1Location(x, y,
				mapId));
		if (castleId != 0) { // ???????????????
			isNowWar = WarTimeController.getInstance().isNowWar(castleId);
		}
		return isNowWar;
	}

	public boolean simWarResult(final L1Character lastAttacker) {
		if (getClanid() == 0) { // ??????????????????????????????
			return false;
		}
		if (Config.SIM_WAR_PENALTY) { // ??????????????????????????????????????????false
			return false;
		}
		L1PcInstance attacker = null;
		String enemyClanName = null;
		boolean sameWar = false;

		if (lastAttacker instanceof L1PcInstance) {
			attacker = (L1PcInstance) lastAttacker;
		} else if (lastAttacker instanceof L1PetInstance) {
			attacker = (L1PcInstance) ((L1PetInstance) lastAttacker)
					.getMaster();
		} else if (lastAttacker instanceof L1SummonInstance) {
			attacker = (L1PcInstance) ((L1SummonInstance) lastAttacker)
					.getMaster();
		} else {
			return false;
		}

		// ???????????????????????????
		for (final L1War war : L1World.getInstance().getWarList()) {
			final L1Clan clan = L1World.getInstance().getClan(getClanname());

			final int warType = war.GetWarType();
			final boolean isInWar = war.CheckClanInWar(getClanname());
			if (attacker != null && attacker.getClanid() != 0) { // lastAttacker???PC?????????????????????????????????????????????
				sameWar = war.CheckClanInSameWar(getClanname(),
						attacker.getClanname());
			}

			if (getId() == clan.getLeaderId() && // ????????????????????????
					warType == 2 && isInWar == true) {
				enemyClanName = war.GetEnemyClanName(getClanname());
				if (enemyClanName != null) {
					war.CeaseWar(getClanname(), enemyClanName); // ??????
				}
			}

			if (warType == 2 && sameWar) {// ?????????????????????????????????????????????????????????????????????
				return true;
			}
		}
		return false;
	}

	public void resExp() {
		final int oldLevel = getLevel();
		final int needExp = ExpTable.getNeedExpNextLevel(oldLevel);
		int exp = 0;
		if (oldLevel < 45) {
			exp = (int) (needExp * 0.05);
		} else if (oldLevel == 45) {
			exp = (int) (needExp * 0.045);
		} else if (oldLevel == 46) {
			exp = (int) (needExp * 0.04);
		} else if (oldLevel == 47) {
			exp = (int) (needExp * 0.035);
		} else if (oldLevel == 48) {
			exp = (int) (needExp * 0.03);
		} else if (oldLevel >= 49) {
			exp = (int) (needExp * 0.025);
		}

		if (exp == 0) {
			return;
		}
		addExp(exp);
	}

	public void resExp1() {
		final int oldLevel = getLevel();
		final int needExp = ExpTable.getNeedExpNextLevel(oldLevel);
		int exp = 0;
		if (oldLevel < 45) {
			exp = (int) (needExp * 0.025);
		} else if (oldLevel == 45) {
			exp = (int) (needExp * 0.0225);
		} else if (oldLevel == 46) {
			exp = (int) (needExp * 0.02);
		} else if (oldLevel == 47) {
			exp = (int) (needExp * 0.0175);
		} else if (oldLevel == 48) {
			exp = (int) (needExp * 0.015);
		} else if (oldLevel >= 49) {
			exp = (int) (needExp * 0.0125);
		}

		if (exp == 0) {
			return;
		}
		addExp(exp);
	}

	public void deathPenalty() {
		final int oldLevel = getLevel();
		final int needExp = ExpTable.getNeedExpNextLevel(oldLevel);
		int exp = 0;
		if (oldLevel >= 1 && oldLevel < 14) {
			exp = 0;
		} else if (oldLevel >= 14 && oldLevel < 45) {
			exp = (int) (needExp * 0.1);
		} else if (oldLevel == 45) {
			exp = (int) (needExp * 0.09);
		} else if (oldLevel == 46) {
			exp = (int) (needExp * 0.08);
		} else if (oldLevel == 47) {
			exp = (int) (needExp * 0.07);
		} else if (oldLevel == 48) {
			exp = (int) (needExp * 0.06);
		} else if (oldLevel >= 49) {
			exp = (int) (needExp * 0.05);
		}

		if (exp == 0) {
			return;
		}
		addExp(-exp);
	}

	private int _etcItemSkillExp = 0;

	public void addEtcItemSkillExp(final int exp) {
		_etcItemSkillExp += exp;
	}

	public int getEtcItemSkillExp() {
		return _etcItemSkillExp;
	}

	private int _etcItemSkillEr = 0;

	public void addEtcItemSkillEr(final int n) {
		_etcItemSkillEr += n;
	}

	public int getEr() {
		if (hasSkillEffect(L1SkillId.STRIKER_GALE)) {
			return 0;
		}

		int er = 0;
		if (isKnight()) {
			er = getLevel() / 4; // ?????????
		} else if (isCrown() || isElf()) {
			er = getLevel() / 8; // ??????????????????
		} else if (isDarkelf()) {
			er = getLevel() / 6; // ??????????????????
		} else if (isWizard()) {
			er = getLevel() / 10; // ???
		} else if (this.isDragonKnight()) {
			er = this.getLevel() / 7; // ?????????

		} else if (this.isIllusionist()) {
			er = this.getLevel() / 9; // ?????????
		}

		er += (getDex() - 8) / 2;

		if (hasSkillEffect(L1SkillId.DRESS_EVASION)) {
			er += 12;
		}
		if (hasSkillEffect(L1SkillId.SOLID_CARRIAGE)) {
			er += 15;
		}

		er += _etcItemSkillEr;

		return er;
	}

	public L1ItemInstance getWeapon() {
		return _weapon;
	}

	public void setWeapon(final L1ItemInstance weapon) {
		_weapon = weapon;
	}

	public L1Quest getQuest() {
		return _quest;
	}

	/**
	 * ??????
	 * @return
	 */
	public boolean isCrown() {
		return (getClassId() == CLASSID_PRINCE || getClassId() == CLASSID_PRINCESS);
	}

	/**
	 * ??????
	 * @return
	 */
	public boolean isKnight() {
		return (getClassId() == CLASSID_KNIGHT_MALE || getClassId() == CLASSID_KNIGHT_FEMALE);
	}

	/**
	 * ??????
	 * @return
	 */
	public boolean isElf() {
		return (getClassId() == CLASSID_ELF_MALE || getClassId() == CLASSID_ELF_FEMALE);
	}

	/**
	 * ??????
	 * @return
	 */
	public boolean isWizard() {
		return (getClassId() == CLASSID_WIZARD_MALE || getClassId() == CLASSID_WIZARD_FEMALE);
	}

	/**
	 * ??????
	 * @return
	 */
	public boolean isDarkelf() {
		return (getClassId() == CLASSID_DARK_ELF_MALE || getClassId() == CLASSID_DARK_ELF_FEMALE);
	}

	/**
	 * ?????????
	 * 
	 * @return
	 */
	public boolean isDragonKnight() {
		return ((this.getClassId() == CLASSID_DRAGON_KNIGHT_MALE) || (this
				.getClassId() == CLASSID_DRAGON_KNIGHT_FEMALE));
	}

	/**
	 * ?????????
	 * 
	 * @return
	 */
	public boolean isIllusionist() {
		return ((this.getClassId() == CLASSID_ILLUSIONIST_MALE) || (this
				.getClassId() == CLASSID_ILLUSIONIST_FEMALE));
	}

	private static final Log _log = LogFactory.getLog(L1PcInstance.class);

	private LineageClient _netConnection;
	private int _classId;
	private int _type;
	private long _exp;
	private final L1Karma _karma = new L1Karma();
	private boolean _gm;
	private boolean _monitor;

	private short _accessLevel;
	private int _currentWeapon;
	private final L1PcInventory _inventory;
	private final L1PcBlessEnchant _blessEnchant;
	// private final L1DwarfInventory _dwarf;
	// private final L1Inventory _tradewindow;
	private L1ItemInstance _weapon;
	private L1Party _party;
	private int _partyID;
	private int _tradeID;
	private boolean _tradeOk;
	private int _tempID;
	private int _tempCount;
	private boolean _isTeleport = false;
	private boolean _isDrink = false;
	private boolean _isGres = false;
	// private boolean _isPinkName = false;
	private final L1Quest _quest;
	private MpRegeneration _mpRegen;
	private MpRegenerationByDoll _mpRegenByDoll;
	private HpRegeneration _hpRegen;
	// private static Timer _regenTimer = new Timer(true);
	private boolean _mpRegenActive;
	private boolean _mpRegenActiveByDoll;
	private boolean _hpRegenActive;
	private final L1EquipmentSlot _equipSlot;
	private final L1GamSpList _gamSpList; // ??????????????????????????????
	private final L1PCAction _action;

	public L1PCAction getAction() {
		return _action;
	}

	private String _accountName; // ??? ????????????????????????

	public String getAccountName() {
		return _accountName;
	}

	public void setAccountName(final String s) {
		_accountName = s;
	}

	private int _baseMaxHp = 0; // ??? ???????????????????????????1???32767???

	public int getBaseMaxHp() {
		return _baseMaxHp;
	}

	public void addBaseMaxHp(int randomHp) {
		randomHp += _baseMaxHp;
		if (randomHp >= 32767) {
			randomHp = 32767;
		} else if (randomHp < 1) {
			randomHp = 1;
		}
		addMaxHp(randomHp - _baseMaxHp);
		_baseMaxHp = randomHp;
	}

	private int _baseMaxMp = 0; // ??? ???????????????????????????0???32767???

	public int getBaseMaxMp() {
		return _baseMaxMp;
	}

	public void addBaseMaxMp(int randomMp) {
		randomMp += _baseMaxMp;
		if (randomMp >= 32767) {
			randomMp = 32767;
		} else if (randomMp < 0) {
			randomMp = 0;
		}
		addMaxMp(randomMp - _baseMaxMp);
		_baseMaxMp = randomMp;
	}

	private int _baseAc = 0; // ??? ??????????????????-128???127???

	public int getBaseAc() {
		return _baseAc;
	}

	private byte _baseStr = 0; // ??? ?????????????????????1???127???

	public byte getBaseStr() {
		return _baseStr;
	}

	public void addBaseStr(byte i) {
		i += _baseStr;
		if (i >= 127) {
			i = 127;
		} else if (i < 1) {
			i = 1;
		}
		addStr((byte) (i - _baseStr));
		_baseStr = i;
	}

	private byte _baseCon = 0; // ??? ?????????????????????1???127???

	public byte getBaseCon() {
		return _baseCon;
	}

	public void addBaseCon(byte i) {
		i += _baseCon;
		if (i >= 127) {
			i = 127;
		} else if (i < 1) {
			i = 1;
		}
		addCon((byte) (i - _baseCon));
		_baseCon = i;
	}

	private byte _baseDex = 0; // ??? ?????????????????????1???127???

	public byte getBaseDex() {
		return _baseDex;
	}

	public void addBaseDex(byte i) {
		i += _baseDex;
		if (i >= 127) {
			i = 127;
		} else if (i < 1) {
			i = 1;
		}
		addDex((byte) (i - _baseDex));
		_baseDex = i;
	}

	private byte _baseCha = 0; // ??? ?????????????????????1???127???

	public byte getBaseCha() {
		return _baseCha;
	}

	public void addBaseCha(byte i) {
		i += _baseCha;
		if (i >= 127) {
			i = 127;
		} else if (i < 1) {
			i = 1;
		}
		addCha((byte) (i - _baseCha));
		_baseCha = i;
	}

	private byte _baseInt = 0; // ??? ?????????????????????1???127???

	public byte getBaseInt() {
		return _baseInt;
	}

	public void addBaseInt(byte i) {
		i += _baseInt;
		if (i >= 127) {
			i = 127;
		} else if (i < 1) {
			i = 1;
		}
		addInt((byte) (i - _baseInt));
		_baseInt = i;
	}

	private byte _baseWis = 0; // ??? ?????????????????????1???127???

	public byte getBaseWis() {
		return _baseWis;
	}

	public void addBaseWis(byte i) {
		i += _baseWis;
		if (i >= 127) {
			i = 127;
		} else if (i < 1) {
			i = 1;
		}
		addWis((byte) (i - _baseWis));
		_baseWis = i;
	}

	private int _baseDmgup = 0; // ??? ??????????????????????????????-128???127???

	public int getBaseDmgup() {
		return _baseDmgup;
	}

	private int _baseHitup = 0; // ??? ????????????????????????-128???127???

	public int getBaseHitup() {
		return _baseHitup;
	}

	private int _baseBowHitup = 0; // ??? ???????????????????????????-128???127???

	public int getBaseBowHitup() {
		return _baseBowHitup;
	}

	private int _baseMr = 0; // ??? ????????????????????????0??????

	public int getBaseMr() {
		return _baseMr;
	}

	private int _advenHp; // ??? // ?????????????????? ??????????????????????????????????????????

	public int getAdvenHp() {
		return _advenHp;
	}

	public void setAdvenHp(final int i) {
		_advenHp = i;
	}

	private int _advenMp; // ??? // ?????????????????? ??????????????????????????????????????????

	public int getAdvenMp() {
		return _advenMp;
	}

	public void setAdvenMp(final int i) {
		_advenMp = i;
	}

	private int _bonusStats; // ??? ??????????????????????????????????????????

	public int getBonusStats() {
		return _bonusStats;
	}

	public void setBonusStats(final int i) {
		_bonusStats = i;
	}

	private int _elixirStats; // ??? ?????????????????????????????????????????????

	public int getElixirStats() {
		return _elixirStats;
	}

	public void setElixirStats(final int i) {
		_elixirStats = i;
	}

	private int _elfAttr; // ??? ??????????????????

	public int getElfAttr() {
		return _elfAttr;
	}

	public void setElfAttr(final int i) {
		_elfAttr = i;
	}

	// ?????????????????????????????????
	private int _PcAttr;

	public int get_PcAttr() {
		return _PcAttr;
	}

	public void set_PcAttr(final int i) {
		_PcAttr = i;
	}

	// ????????????????????????????????? end

	private int _expRes; // ??? EXP??????

	public int getExpRes() {
		return _expRes;
	}

	public void setExpRes(final int i) {
		_expRes = i;
	}

	private int _partnerId = 0; // ??? ????????????

	public int getPartnerId() {
		return _partnerId;
	}

	public void setPartnerId(final int i) {
		_partnerId = i;
	}

	private int _onlineStatus; // ??? ?????????????????????

	public int getOnlineStatus() {
		return _onlineStatus;
	}

	public void setOnlineStatus(final int i) {
		_onlineStatus = i;
	}

	private int _homeTownId; // ??? ??????????????????

	public int getHomeTownId() {
		return _homeTownId;
	}

	public void setHomeTownId(final int i) {
		_homeTownId = i;
	}

	private int _contribution; // ??? ?????????

	public int getContribution() {
		return _contribution;
	}

	public void setContribution(final int i) {
		_contribution = i;
	}

	// ????????????????????????????????????
	private int _hellTime;

	public int getHellTime() {
		return _hellTime;
	}

	public void setHellTime(final int i) {
		_hellTime = i;
	}

	private boolean _banned; // ??? ??????

	public boolean isBanned() {
		return _banned;
	}

	public void setBanned(final boolean flag) {
		_banned = flag;
	}

	private int _food; // ??? ?????????

	public int get_food() {
		return _food;
	}

	public void set_food(final int i) {
		_food = i;
		if (_food > 225) {
			_food = 225;
		}
		_food = i;
		if (_food == 225) {// LOLI ????????????
			final Calendar cal = Calendar.getInstance();
			long h_time = cal.getTimeInMillis() / 1000;// ????????????
			set_h_time(h_time);// ??????????????????

		} else {
			set_h_time(-1);// ??????????????????
		}
	}

	public L1EquipmentSlot getEquipSlot() {
		return _equipSlot;
	}

	public static L1PcInstance load(final String charName) {
		L1PcInstance result = null;
		try {
			result = CharacterTable.getInstance().loadCharacter(charName);
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return result;
	}

	/**
	 * ??????????????????????????????????????????????????????????????????
	 * 
	 * @throws Exception
	 */
	public void save() throws Exception {
		if (isGhost()) {
			WriteLogTxt.Recording("??????????????????????????????", "??????:" + this.getName()
					+ " ??????????????????  ??????:isGhost");
			return;
		}

		CharacterTable.getInstance().storeCharacter(this);
	}

	/**
	 * ???????????????????????????????????????????????????????????????????????????????????????????????????
	 */
	public void saveInventory() {
		for (final L1ItemInstance item : getInventory().getItems()) {
			getInventory().saveItem(item, item.getRecordingColumns());
		}
	}

	public static final int REGENSTATE_NONE = 4;
	public static final int REGENSTATE_MOVE = 2;
	public static final int REGENSTATE_ATTACK = 1;

	public void setRegenState(final int state) {
		if (_mpRegen != null) {
			_mpRegen.setState(state);
		}
		if (_hpRegen != null) {
			_hpRegen.setState(state);
		}
	}

	public MpRegeneration getMpRegeneration() {
		return _mpRegen;
	}

	public HpRegeneration getHpRegeneration() {
		return _hpRegen;
	}

	public double getMaxWeight() {
		final int str = getStr();
		final int con = getCon();
		double maxWeight = (150 * (Math.floor(0.6 * str + 0.4 * con + 1)))
				* get_weightUPByDoll();
		// double maxWeight = 1500 + 150 * ((str + con - 18) / 2);

		final int weightReductionByArmor = getWeightReduction(); // ???????????????????????????

		int weightReductionByDoll = 0; // ??????????????????????????????????????????
		final Object[] dollList = getDollList().values().toArray();
		for (final Object dollObject : dollList) {
			final L1DollInstance doll = (L1DollInstance) dollObject;
			weightReductionByDoll += doll.getWeightReductionByDoll();
		}

		int weightReductionByMagic = 0;
		if (hasSkillEffect(L1SkillId.DECREASE_WEIGHT)) { // ??????????????????????????????
			weightReductionByMagic = 10;
		}

		double originalWeightReduction = 1; // ???????????????????????????????????????????????????
		originalWeightReduction += 0.04 * (getOriginalStrWeightReduction() + getOriginalConWeightReduction());

		final int weightReduction = weightReductionByArmor
				+ weightReductionByDoll + weightReductionByMagic;
		maxWeight += ((maxWeight / 100) * weightReduction);

		maxWeight *= Config.RATE_WEIGHT_LIMIT; // ??????????????????????????????

		maxWeight *= originalWeightReduction;

		return maxWeight;
	}

	public boolean isFastMovable() {
		return (hasSkillEffect(L1SkillId.HOLY_WALK)
				|| hasSkillEffect(L1SkillId.MOVING_ACCELERATION) || hasSkillEffect(L1SkillId.WIND_WALK));
	}

	public boolean isBrave() {
		return hasSkillEffect(L1SkillId.STATUS_BRAVE);
	}

	public boolean isHaste() {
		return (hasSkillEffect(L1SkillId.STATUS_HASTE)
				|| hasSkillEffect(L1SkillId.HASTE)
				|| hasSkillEffect(L1SkillId.GREATER_HASTE) || getMoveSpeed() == 1);
	}

	private int invisDelayCounter = 0;

	public boolean isInvisDelay() {
		return (invisDelayCounter > 0);
	}

	private final Object _invisTimerMonitor = new Object();

	public void addInvisDelayCounter(final int counter) {
		synchronized (_invisTimerMonitor) {
			invisDelayCounter += counter;
		}
	}

	private static final long DELAY_INVIS = 3000L;

	public void beginInvisTimer() {
		addInvisDelayCounter(1);
		GeneralThreadPool.getInstance().pcSchedule(new L1PcInvisDelay(this),
				DELAY_INVIS);
	}

	/*
	 * private long _oldMoveTimeInMillis = 0L; private int _moveInjustice = 0;
	 * 
	 * public void checkMoveInterval() { long nowMoveTimeInMillis =
	 * System.currentTimeMillis();
	 * 
	 * long moveInterval = nowMoveTimeInMillis - _oldMoveTimeInMillis; byte
	 * speed = (byte) 16; // ???????????? L1WilliamPlayerSpeed Player_Speed =
	 * PlayerSpeed.getInstance().getTemplate(getTempCharGfx()); if (Player_Speed
	 * != null) { switch(getCurrentWeapon()) { case 0: { // ?????? if
	 * (Player_Speed.getMove_0() != 0) { speed = (byte)
	 * Player_Speed.getMove_0(); } } break; case 4: { // ????????? if
	 * (Player_Speed.getMove_4() != 0) { speed = (byte)
	 * Player_Speed.getMove_4(); } } break; case 11: { // ?????? if
	 * (Player_Speed.getMove_11() != 0) { speed = (byte)
	 * Player_Speed.getMove_11(); } } break; case 20: { // ?????? if
	 * (Player_Speed.getMove_20() != 0) { speed = (byte)
	 * Player_Speed.getMove_20(); } } break; case 24: { // ?????? if
	 * (Player_Speed.getMove_24() != 0) { speed = (byte)
	 * Player_Speed.getMove_24(); } } break; case 40: { // ?????? if
	 * (Player_Speed.getMove_40() != 0) { speed = (byte)
	 * Player_Speed.getMove_40(); } } break; case 46: { // ?????? if
	 * (Player_Speed.getMove_46() != 0) { speed = (byte)
	 * Player_Speed.getMove_46(); } } break; case 50: { // ????????? if
	 * (Player_Speed.getMove_50() != 0) { speed = (byte)
	 * Player_Speed.getMove_50(); } } break; case 54: { // ?????? if
	 * (Player_Speed.getMove_54() != 0) { speed = (byte)
	 * Player_Speed.getMove_54(); } } break; case 58: { // ?????? if
	 * (Player_Speed.getMove_58() != 0) { speed = (byte)
	 * Player_Speed.getMove_58(); } } break; case 62: { // ????????? if
	 * (Player_Speed.getMove_62() != 0) { speed = (byte)
	 * Player_Speed.getMove_62(); } } break; }
	 * 
	 * if (Player_Speed.getMoveDouble() != 0) {
	 * switch(Player_Speed.getMoveDouble()) { case 16: { // 110.(16) speed *=
	 * 1.5; } break; case 36: { // 110.(36) speed /= 1.5; } break; case 48: { //
	 * 110.(48) speed /= 2; } break; case 54: { // 110.(54) speed /= 2.25; }
	 * break; case 60: { // 110.(60) speed /= 2.5; } break; case 72: { //
	 * 110.(72) speed /= 3; } break; case 84: { // 110.(84) speed /= 3.5; }
	 * break; case 96: { // 110.(96) speed /= 4; } break; } } }
	 * 
	 * double MoveSpeed = speed * 40; // ???????????? = ???????????? * 40
	 * 
	 * if (hasSkillEffect(L1SkillId.SLOW) || hasSkillEffect(L1SkillId.MASS_SLOW)
	 * || hasSkillEffect(L1SkillId.ENTANGLE) || getMoveSpeed() == 2) { //
	 * ???????????????????????? MoveSpeed = (MoveSpeed * 2); } if
	 * (hasSkillEffect(L1SkillId.STATUS_HASTE) ||
	 * hasSkillEffect(L1SkillId.HASTE) ||
	 * hasSkillEffect(L1SkillId.GREATER_HASTE) || getMoveSpeed() == 1) { //
	 * ??????????????? MoveSpeed = (MoveSpeed / 1.33); } if
	 * (hasSkillEffect(L1SkillId.STATUS_BRAVE) ||
	 * hasSkillEffect(L1SkillId.HOLY_WALK) ||
	 * hasSkillEffect(L1SkillId.MOVING_ACCELERATION) ||
	 * hasSkillEffect(L1SkillId.WIND_WALK)) { // ????????????????????????????????? MoveSpeed =
	 * (MoveSpeed / 1.5); }
	 * 
	 * //sendPackets(new S_ServerMessage(166, "???????????????(" + MoveSpeed + ")"));
	 * //sendPackets(new S_ServerMessage(166, "???????????????(" + moveInterval + ")"));
	 * 
	 * if (MoveSpeed >= moveInterval) { // ???????????????????????????????????? _moveInjustice++;
	 * //sendPackets(new S_ServerMessage(166, "???????????????(" + _moveInjustice + ")"));
	 * if (_moveInjustice >= 5) {
	 * _log.info(L1WilliamSystemMessage.ShowMessage(1089) + " (" + getName() +
	 * ") " + L1WilliamSystemMessage.ShowMessage(1090)); // ??????????????????
	 * writeInfo(L1WilliamSystemMessage.ShowMessage(1094) + ": (" + getName() +
	 * ") " + L1WilliamSystemMessage.ShowMessage(1095) + "???" +
	 * L1WilliamSystemMessage.ShowMessage(1091) + ": (" + getLastOnline() + ")???"
	 * + " Poly: (" + getTempCharGfx() + ")???"); // ?????????????????? end if (!isGm()) { //
	 * ?????????????????? BroadCastToAll(L1WilliamSystemMessage.ShowMessage(1089) + " (" +
	 * getName() + ") " + L1WilliamSystemMessage.ShowMessage(1090)); // ??????????????????
	 * end sendPackets(new S_Disconnect()); } _moveInjustice = 0; } } else { //
	 * ??????????????????????????? _moveInjustice = 0; }
	 * 
	 * _oldMoveTimeInMillis = nowMoveTimeInMillis; }
	 * 
	 * private long _oldAttackTimeInMillis = 0L; private int _attackInjustice =
	 * 0;
	 * 
	 * public int getAttackInjustice() { return _attackInjustice; }
	 * 
	 * public void checkAttackInterval() { long nowAttackTimeInMillis =
	 * System.currentTimeMillis(); long attckInterval = nowAttackTimeInMillis -
	 * _oldAttackTimeInMillis; byte speed = (byte) 24; // ????????????
	 * L1WilliamPlayerSpeed Player_Speed =
	 * PlayerSpeed.getInstance().getTemplate(getTempCharGfx()); if (Player_Speed
	 * != null) { switch(getCurrentWeapon()) { case 0: { // ?????? if
	 * (Player_Speed.getAtk_0() != 0) { speed = (byte) Player_Speed.getAtk_0();
	 * } } break; case 4: { // ????????? if (Player_Speed.getAtk_4() != 0) { speed =
	 * (byte) Player_Speed.getAtk_4(); } } break; case 11: { // ?????? if
	 * (Player_Speed.getAtk_11() != 0) { speed = (byte)
	 * Player_Speed.getAtk_11(); } } break; case 20: { // ?????? if
	 * (Player_Speed.getAtk_20() != 0) { speed = (byte)
	 * Player_Speed.getAtk_20(); } } break; case 24: { // ?????? if
	 * (Player_Speed.getAtk_24() != 0) { speed = (byte)
	 * Player_Speed.getAtk_24(); } } break; case 40: { // ?????? if
	 * (Player_Speed.getAtk_40() != 0) { speed = (byte)
	 * Player_Speed.getAtk_40(); } } break; case 46: { // ?????? if
	 * (Player_Speed.getAtk_46() != 0) { speed = (byte)
	 * Player_Speed.getAtk_46(); } } break; case 50: { // ????????? if
	 * (Player_Speed.getAtk_50() != 0) { speed = (byte)
	 * Player_Speed.getAtk_50(); } } break; case 54: { // ?????? if
	 * (Player_Speed.getAtk_54() != 0) { speed = (byte)
	 * Player_Speed.getAtk_54(); } } break; case 58: { // ?????? if
	 * (Player_Speed.getAtk_58() != 0) { speed = (byte)
	 * Player_Speed.getAtk_58(); } } break; case 62: { // ????????? if
	 * (Player_Speed.getAtk_62() != 0) { speed = (byte)
	 * Player_Speed.getAtk_62(); } } break; }
	 * 
	 * if (Player_Speed.getAtkDouble() != 0) {
	 * switch(Player_Speed.getAtkDouble()) { case 16: { speed *= 1.5; } break;
	 * case 36: { speed /= 1.5; } break; case 48: { speed /= 2; } break; case
	 * 54: { speed /= 2.25; } break; case 60: { speed /= 2.5; } break; case 72:
	 * { speed /= 3; } break; case 84: { speed /= 3.5; } break; case 96: { speed
	 * /= 4; } break; } } }
	 * 
	 * double AtkSpeed = speed * 40; // ???????????? = ???????????? * 40
	 * 
	 * if (hasSkillEffect(L1SkillId.SLOW) || hasSkillEffect(L1SkillId.MASS_SLOW)
	 * || hasSkillEffect(L1SkillId.ENTANGLE) || getMoveSpeed() == 2) { //
	 * ???????????????????????? AtkSpeed = (AtkSpeed * 2); } if
	 * (hasSkillEffect(L1SkillId.STATUS_HASTE) ||
	 * hasSkillEffect(L1SkillId.HASTE) ||
	 * hasSkillEffect(L1SkillId.GREATER_HASTE) || getMoveSpeed() == 1) { //
	 * ??????????????? AtkSpeed = (AtkSpeed / 1.33); } if
	 * (hasSkillEffect(L1SkillId.STATUS_BRAVE)) { // ?????? AtkSpeed = (AtkSpeed /
	 * 1.5); }
	 * 
	 * //sendPackets(new S_ServerMessage(166, "???????????????(" + AtkSpeed + ")"));
	 * //sendPackets(new S_ServerMessage(166, "???????????????(" + attckInterval + ")"));
	 * 
	 * if (AtkSpeed >= attckInterval) { // ???????????????????????????????????? _attackInjustice++;
	 * //sendPackets(new S_ServerMessage(166, "???????????????(" + _attackInjustice +
	 * ")")); if (_attackInjustice >= 5) {
	 * _log.info(L1WilliamSystemMessage.ShowMessage(1089) + " (" + getName() +
	 * ") " + L1WilliamSystemMessage.ShowMessage(1092)); // ??????????????????
	 * writeInfo(L1WilliamSystemMessage.ShowMessage(1094) + ": (" + getName() +
	 * ") " + L1WilliamSystemMessage.ShowMessage(1096) + "???" +
	 * L1WilliamSystemMessage.ShowMessage(1091) + ": (" + getLastOnline() + ")???"
	 * + " Poly: (" + getTempCharGfx() + ")???"); // ?????????????????? end if (!isGm()) { //
	 * ?????????????????? BroadCastToAll(L1WilliamSystemMessage.ShowMessage(1089) + " (" +
	 * getName() + ") " + L1WilliamSystemMessage.ShowMessage(1092)); // ??????????????????
	 * end sendPackets(new S_Disconnect()); } _attackInjustice = 0; } } else {
	 * // ??????????????????????????? _attackInjustice = 0; } _oldAttackTimeInMillis =
	 * nowAttackTimeInMillis; }
	 */

	public void addExp(final int exp) {
		synchronized (this) {
			_exp += exp;
			if (_exp > ExpTable.MAX_EXP) {
				_exp = ExpTable.MAX_EXP;
			}
			onChangeExp();
		}
	}

	public synchronized void addContribution(final int contribution) {
		_contribution += contribution;
	}

	/*
	 * public void beginExpMonitor() { _expMonitorFuture =
	 * GeneralThreadPool.getInstance() .pcScheduleAtFixedRate(new
	 * L1PcExpMonitor(this), 0L, INTERVAL_EXP_MONITOR); }
	 */
	private void levelUp(final int gap) {
		// int level = getLevel();
		resetLevel();

		// ????????????????????????
		if (getLevel() == 99 && Config.ALT_REVIVAL_POTION) {
			try {
				final L1Item l1item = ItemTable.getInstance()
						.getTemplate(43000);
				if (l1item != null) {
					getInventory().storeItem(43000, 1);
					sendPackets(new S_ServerMessage(403, l1item.getName()));
				} else {
					sendPackets(new S_SystemMessage("???????????????????????????"));
				}
			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);
				sendPackets(new S_SystemMessage("???????????????????????????"));
			}
		}

		for (int i = 0; i < gap; i++) {
			// System.out.println("??????:"+i+" gap:"+gap);
			final int randomHp = CalcStat.calcStatHp(getType(), getBaseMaxHp(),
					getBaseCon());
			final int randomMp = CalcStat.calcStatMp(getType(), getBaseMaxMp(),
					getBaseWis());
			/*
			 * int newhp = getBaseMaxHp() + randomHp; int newmp = getBaseMaxMp()
			 * + randomMp; level += i; L1PcHpMp pcHpMp; if
			 * (_levelhpmpup.containsKey(getId())) { pcHpMp =
			 * _levelhpmpup.get(getId()); if
			 * (_levelhpmpup.get(getId()).getHp().containsKey(level)) { //
			 * System
			 * .out.println("?????????????????????????????????????????????"+_levelhpmpup.get(getId()).getHp
			 * ().get(level)+"  ?????????"+level); if (newhp >
			 * _levelhpmpup.get(getId()).getHp().get(level)) { //
			 * System.out.println("??????newhp??????"+
			 * _levelhpmpup.get(getId()).getHp().get(level)); randomHp =
			 * _levelhpmpup.get(getId()).getHp().get(level) - getBaseMaxHp(); }
			 * }else { pcHpMp.puthpmp(level, newhp, newmp);
			 * _levelhpmpup.put(getId(), pcHpMp); //
			 * System.out.println("???????????????????????????"+newhp+"   "+newmp+"  ?????????"+level);
			 * } if (_levelhpmpup.get(getId()).getMp().containsKey(level)) { //
			 * System
			 * .out.println("?????????????????????????????????????????????"+_levelhpmpup.get(getId()).getMp
			 * ().get(level)+"  ?????????"+level); if (newmp >
			 * _levelhpmpup.get(getId()).getMp().get(level)) { //
			 * System.out.println("??????newmp??????"+
			 * _levelhpmpup.get(getId()).getMp().get(level)); randomMp =
			 * _levelhpmpup.get(getId()).getMp().get(level) - getBaseMaxMp(); }
			 * }else { pcHpMp.puthpmp(level, newhp, newmp);
			 * _levelhpmpup.put(getId(), pcHpMp); //
			 * System.out.println("???????????????????????????"+newhp+"   "+newmp+"  ?????????"+level);
			 * }
			 * 
			 * }else { pcHpMp = new L1PcHpMp(newhp, newmp, level); //
			 * System.out.println("???????????????????????????"+newhp+"   "+newmp+"  ?????????"+level);
			 * _levelhpmpup.put(getId(), pcHpMp); }
			 */
			addBaseMaxHp(randomHp);
			addBaseMaxMp(randomMp);
			// ???????????????
			setCurrentHp(getMaxHp());
			setCurrentMp(getMaxMp());
			// ??????????????? end
		}
		resetBaseHitup();
		resetBaseDmgup();
		resetBaseAc();
		resetBaseMr();

		try {
			// DB??????????????????????????????????????????
			save();
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		// ???????????????????????????
		if (getLevel() >= 51 && getLevel() - 50 > getBonusStats()) {
			if ((getBaseStr() + getBaseDex() + getBaseCon() + getBaseInt()
					+ getBaseWis() + getBaseCha()) < (Config.BONUS_STATS1 * 6)) { // ?????????????????????
				sendPackets(new S_bonusstats(getId(), 1));
			}
		}
		sendPackets(new S_OwnCharStatus(this));
		Reward.getInstance().getItem(this);
		// l1j.william.Reward.getItem(this); // ??????????????????
	}

	private void levelDown(final int gap) {
		// int level = getLevel();
		resetLevel();

		for (int i = 0; i > gap; i--) {
			// ?????????????????????????????????????????????????????????????????????????????????base??????0?????????
			final int randomHp = CalcStat
					.calcStatHp(getType(), 0, getBaseCon());
			final int randomMp = CalcStat
					.calcStatMp(getType(), 0, getBaseWis());
			// level -= i;
			/*
			 * if (_levelhpup.get(level)!=null) { randomHp =
			 * _levelhpup.get(getLevel()); } if (_levelmpup.get(level)!=null) {
			 * randomMp = _levelmpup.get(getLevel()); } _levelhpup.put(level,
			 * randomHp); _levelmpup.put(level, randomMp);
			 */
			addBaseMaxHp(-randomHp);
			addBaseMaxMp(-randomMp);
		}
		resetBaseHitup();
		resetBaseDmgup();
		resetBaseAc();
		resetBaseMr();

		try {
			// DB??????????????????????????????????????????
			save();
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		sendPackets(new S_OwnCharStatus(this));
	}

	public void beginGameTimeCarrier() {
		new L1GameTimeCarrier(this).start();
	}

	/*
	 * private boolean _ghost = false; // ????????????
	 * 
	 * public boolean isGhost() { return _ghost; }
	 * 
	 * private void setGhost(boolean flag) { _ghost = flag; }
	 */

	private boolean _ghostCanTalk = true; // NPC???????????????????????????

	public boolean isGhostCanTalk() {
		return _ghostCanTalk;
	}

	private void setGhostCanTalk(final boolean flag) {
		_ghostCanTalk = flag;
	}

	public void beginGhost(final int locx, final int locy, final short mapid,
			final boolean canTalk) {
		beginGhost(locx, locy, mapid, canTalk, 0);
	}

	public void beginGhost(final int locx, final int locy, final short mapid,
			final boolean canTalk, final int sec) {
		if (isGhost()) {
			return;
		}
		setGhost(true);
		_ghostSaveLocX = getX();
		_ghostSaveLocY = getY();
		_ghostSaveMapId = getMapId();
		_ghostSaveHeading = getHeading();
		setGhostCanTalk(canTalk);
		L1Teleport.teleport(this, locx, locy, mapid, 5, true);
		if (sec > 0) {
			_ghostFuture = GeneralThreadPool.getInstance().pcSchedule(
					new L1PcGhostMonitor(this), sec * 1000);
		}
	}

	public void endGhost() {
		setGhost(false);
		setGhostCanTalk(true);
		L1Teleport.teleport(this, _ghostSaveLocX, _ghostSaveLocY,
				_ghostSaveMapId, _ghostSaveHeading, true);
	}

	private ScheduledFuture<?> _ghostFuture;

	private int _ghostSaveLocX = 0;
	private int _ghostSaveLocY = 0;
	private short _ghostSaveMapId = 0;
	private int _ghostSaveHeading = 0;

	private ScheduledFuture<?> _hellFuture;

	public void beginHell(final boolean isFirst) {
		/*
		 * if (getMapId() != 666) { final int locx = 32701; final int locy =
		 * 32777; final short mapid = 666; L1Teleport.teleport(this, locx, locy,
		 * mapid, 5, false); }
		 * 
		 * if (isFirst) { setHellTime(300); sendPackets(new S_BlueMessage(552,
		 * String.valueOf(get_PKcount()), String.valueOf(getHellTime() / 60)));
		 * } else { sendPackets(new S_BlueMessage(637,
		 * String.valueOf(getHellTime()))); } if (_hellFuture == null) {
		 * _hellFuture = GeneralThreadPool .getInstance()
		 * .pcScheduleAtFixedRate(new L1PcHellMonitor(this), 0L, 1000L); }
		 */
	}

	public void endHell() {
		if (_hellFuture != null) {
			_hellFuture.cancel(false);
			_hellFuture = null;
		}
		// ?????????????????????????????????????????????????????????
		final int[] loc = L1TownLocation
				.getGetBackLoc(L1TownLocation.TOWNID_ORCISH_FOREST);
		L1Teleport.teleport(this, loc[0], loc[1], (short) loc[2], 5, true);
		try {
			save();
		} catch (final Exception ignore) {
			// ignore
		}
	}

	@Override
	public void setPoisonEffect(final int effectId) {
		sendPackets(new S_Poison(getId(), effectId));

		if (!isGmInvis() && !isGhost() && !isInvisble()) {
			broadcastPacket(new S_Poison(getId(), effectId));
		}
	}

	@Override
	public void healHp(final int pt) {
		super.healHp(pt);

		sendPackets(new S_HPUpdate(this));
	}

	@Override
	public int getKarma() {
		return _karma.get();
	}

	@Override
	public void setKarma(final int i) {
		_karma.set(i);
	}

	public void addKarma(final int i) {
		synchronized (_karma) {
			_karma.add(i);
		}
	}

	public int getKarmaLevel() {
		return _karma.getLevel();
	}

	public int getKarmaPercent() {
		return _karma.getPercent();
	}

	private Timestamp _lastPk;

	/**
	 * ????????????????????????PK??????????????????
	 * 
	 * @return _lastPk
	 * 
	 */
	public Timestamp getLastPk() {
		return _lastPk;
	}

	/**
	 * ????????????????????????PK????????????????????????
	 * 
	 * @param time
	 *            ??????PK?????????Timestamp?????? ?????????????????????null?????????
	 */
	public void setLastPk(final Timestamp time) {
		_lastPk = time;
	}

	/**
	 * ????????????????????????PK??????????????????????????????????????????
	 */
	public void setLastPk() {
		_lastPk = new Timestamp(System.currentTimeMillis());
	}

	/**
	 * ???????????????
	 * 
	 * @return ????????????????????????true
	 */
	public boolean isWanted() {
		if (_lastPk == null) {
			return false;
		} else if (System.currentTimeMillis() - _lastPk.getTime() > 24 * 3600 * 1000) {
			setLastPk(null);
			return false;
		}
		return true;
	}

	@Override
	public int getMagicLevel() {
		return getClassFeature().getMagicLevel(getLevel());
	}

	private int _weightReduction = 0;

	public int getWeightReduction() {
		return _weightReduction;
	}

	public void addWeightReduction(final int i) {
		_weightReduction += i;
	}

	private int _hasteItemEquipped = 0;

	public int getHasteItemEquipped() {
		return _hasteItemEquipped;
	}

	public void addHasteItemEquipped(final int i) {
		_hasteItemEquipped += i;
	}

	public void removeHasteSkillEffect() {
		if (hasSkillEffect(L1SkillId.SLOW)) {
			removeSkillEffect(L1SkillId.SLOW);
		}
		if (hasSkillEffect(L1SkillId.MASS_SLOW)) {
			removeSkillEffect(L1SkillId.MASS_SLOW);
		}
		if (hasSkillEffect(L1SkillId.ENTANGLE)) {
			removeSkillEffect(L1SkillId.ENTANGLE);
		}
		if (hasSkillEffect(L1SkillId.HASTE)) {
			removeSkillEffect(L1SkillId.HASTE);
		}
		if (hasSkillEffect(L1SkillId.GREATER_HASTE)) {
			removeSkillEffect(L1SkillId.GREATER_HASTE);
		}
		if (hasSkillEffect(L1SkillId.STATUS_HASTE)) {
			removeSkillEffect(L1SkillId.STATUS_HASTE);
		}
	}

	private int _damageReductionByArmor = 0; // ?????????????????????????????????

	public int getDamageReductionByArmor() {
		return _damageReductionByArmor;
	}

	public void addDamageReductionByArmor(final int i) {
		_damageReductionByArmor += i;
	}

	private int _damageReduction = 0;

	public int getDamageReduction() {
		return _damageReduction;
	}

	public void addDamageReduction(final int i, final int r) {
		_damageReduction += i;
		_damageReductionrandom += r;
	}

	private int _damageReductionrandom = 0;

	public int getDamageReductionRandom() {
		return _damageReductionrandom;
	}

	private int _damageUpByHelm = 0;

	public int getDamageUpByHelm() {
		return _damageUpByHelm;
	}

	public void addDamageUpAndRandomByHelm(final int i, final int r) {
		_damageUpByHelm += i;
		_damageUpRandomByHelm += r;
	}

	private int _damageUpRandomByHelm = 0;

	public int getDamageUpRandomByHelm() {
		return _damageUpRandomByHelm;
	}

	private int _damageReductionByRing = 0;

	public int getDamageReductionByRing() {
		return _damageReductionByRing;
	}

	public void addDamageReductionByRing(final int i) {
		_damageReductionByRing += i;
	}

	private int _bowHitRate = 0; // ??????????????????????????????

	public int getBowHitRate() {
		return _bowHitRate;
	}

	public void addBowHitRate(final int i) {
		_bowHitRate += i;
	}

	private boolean _gresValid; // G-RES????????????

	private void setGresValid(final boolean valid) {
		_gresValid = valid;
	}

	public boolean isGresValid() {
		return _gresValid;
	}

	private long _fishingTime = 0;

	public long getFishingTime() {
		return _fishingTime;
	}

	public void setFishingTime(final long i) {
		_fishingTime = i;
	}

	private boolean _isFishing = false;

	public boolean isFishing() {
		return _isFishing;
	}

	public void setFishing(final boolean flag) {
		_isFishing = flag;
	}

	private boolean _isFishingReady = false;

	public boolean isFishingReady() {
		return _isFishingReady;
	}

	public void setFishingReady(final boolean flag) {
		_isFishingReady = flag;
	}

	private int _cookingId = 0;

	public int getCookingId() {
		return _cookingId;
	}

	public void setCookingId(final int i) {
		_cookingId = i;
	}

	private int _dessertId = 0;

	public int getDessertId() {
		return _dessertId;
	}

	public void setDessertId(final int i) {
		_dessertId = i;
	}

	/**
	 * LV?????????????????????????????????????????? LV???????????????????????????????????????????????????????????????
	 * 
	 * @return
	 */
	public void resetBaseDmgup() {
		int newBaseDmgup = 0;
		if (isKnight()) { // ?????????
			newBaseDmgup = getLevel() / 10;
		} else if (isElf()) { // ?????????
			newBaseDmgup = getLevel() / 10;
		} else if (isDarkelf()) { // ??????????????????
			newBaseDmgup = getLevel() / 10;
		}
		addDmgup(newBaseDmgup - _baseDmgup);
		_baseDmgup = newBaseDmgup;
	}

	/**
	 * LV?????????????????????????????????????????? LV???????????????????????????????????????????????????????????????
	 * 
	 * @return
	 */
	public void resetBaseHitup() {
		int newBaseHitup = 0;
		int newBaseBowHitup = 0;
		if (isCrown()) { // ??????
			newBaseHitup = getLevel() / 5;
			newBaseBowHitup = getLevel() / 5;
		} else if (isKnight()) { // ?????????
			newBaseHitup = getLevel() / 3;
			newBaseBowHitup = getLevel() / 3;
		} else if (isElf()) { // ?????????
			newBaseHitup = getLevel() / 5;
			newBaseBowHitup = getLevel() / 5;
		} else if (isDarkelf()) { // ??????????????????
			newBaseHitup = getLevel() / 3;
			newBaseBowHitup = getLevel() / 3;
		} else if (this.isDragonKnight()) { // ?????????????????????
			newBaseHitup = this.getLevel() / 3;
			newBaseBowHitup = this.getLevel() / 3;

		} else if (this.isIllusionist()) { // ???????????????????????????
			newBaseHitup = this.getLevel() / 5;
			newBaseBowHitup = this.getLevel() / 5;
		}
		addHitup(newBaseHitup - _baseHitup);
		addBowHitup(newBaseBowHitup - _baseBowHitup);
		_baseHitup = newBaseHitup;
		_baseBowHitup = newBaseBowHitup;
	}

	/**
	 * ???????????????????????????????????????AC?????????????????????????????? ??????????????????LVUP,LVDown????????????????????????
	 */
	public void resetBaseAc() {
		final int newAc = CalcStat.calcAc(getLevel(), getBaseDex());
		addAc(newAc - _baseAc);
		_baseAc = newAc;
	}

	/**
	 * ?????????????????????????????????????????????MR?????????????????????????????? ???????????????????????????????????????LVUP,LVDown??????????????????
	 */
	public void resetBaseMr() {
		int newMr = 0;
		if (isCrown()) { // ??????
			newMr = 10;
		} else if (isElf()) { // ?????????
			newMr = 25;
		} else if (isWizard()) { // ???????????????
			newMr = 15;
		} else if (isDarkelf()) { // ??????????????????
			newMr = 10;
		}
		newMr += CalcStat.calcStatMr(getWis()); // WIS??????MR????????????
		newMr += getLevel() / 2; // LV?????????????????????
		addMr(newMr - _baseMr);
		_baseMr = newMr;
	}

	/**
	 * EXP???????????????Lv?????????????????????????????? ??????????????????????????????LVUP??????????????????
	 */
	private void resetLevel() {
		setLevel(ExpTable.getLevelByExp(_exp));

		if (_hpRegen != null) {
			_hpRegen.updateLevel();
		}
	}

	public void refresh() {
		resetLevel();
		resetBaseHitup();
		resetBaseDmgup();
		resetBaseMr();
		resetBaseAc();
		this.resetOriginalStrWeightReduction();
		this.resetOriginalConWeightReduction();
	}

	private int _originalStrWeightReduction = 0;

	public int getOriginalStrWeightReduction() {
		return this._originalStrWeightReduction;
	}

	private int _originalConWeightReduction = 0; // ??? ???????????????CON ????????????

	public int getOriginalConWeightReduction() {
		return this._originalConWeightReduction;
	}

	private void resetOriginalStrWeightReduction() {
		final int originalStr = this.getOriginalStr();
		if (this.isCrown()) {
			switch (originalStr) {
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
				this._originalStrWeightReduction = 0;
				break;

			case 14:
			case 15:
			case 16:
				this._originalStrWeightReduction = 1;
				break;

			case 17:
			case 18:
			case 19:
				this._originalStrWeightReduction = 2;
				break;

			default:// 20 UP
				this._originalStrWeightReduction = 3;
				break;
			}

		} else if (this.isKnight()) {
			this._originalStrWeightReduction = 0;
		} else if (this.isElf()) {
			switch (originalStr) {
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
			case 15:
				this._originalStrWeightReduction = 0;
				break;

			default:// 16 UP
				this._originalStrWeightReduction = 2;
				break;
			}

		} else if (this.isDarkelf()) {
			switch (originalStr) {
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
				this._originalStrWeightReduction = 0;
				break;

			case 13:
			case 14:
			case 15:
				this._originalStrWeightReduction = 2;
				break;

			default:// 16 UP
				this._originalStrWeightReduction = 3;
				break;
			}

		} else if (this.isWizard()) {
			switch (originalStr) {
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
				this._originalStrWeightReduction = 0;
				break;

			default:// 9 UP
				this._originalStrWeightReduction = 1;
				break;
			}

		} else if (this.isDragonKnight()) {
			switch (originalStr) {
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
			case 15:
				this._originalStrWeightReduction = 0;
				break;

			default:// 16 UP
				this._originalStrWeightReduction = 1;
				break;
			}

		} else if (this.isIllusionist()) {
			switch (originalStr) {
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
			case 15:
			case 16:
			case 17:
				this._originalStrWeightReduction = 0;
				break;

			default:// 18 UP
				this._originalStrWeightReduction = 1;
				break;
			}
		}
	}

	public void resetOriginalConWeightReduction() {
		final int originalCon = this.getOriginalCon();
		if (this.isCrown()) {
			switch (originalCon) {
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
				this._originalConWeightReduction = 0;
				break;

			default:// 11 UP
				this._originalConWeightReduction = 1;
				break;
			}

		} else if (this.isKnight()) {
			switch (originalCon) {
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
				this._originalConWeightReduction = 0;
				break;

			default:// 15 UP
				this._originalConWeightReduction = 1;
				break;
			}

		} else if (this.isElf()) {
			switch (originalCon) {
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
				this._originalConWeightReduction = 0;
				break;

			default:// 15 UP
				this._originalConWeightReduction = 2;
				break;
			}

		} else if (this.isDarkelf()) {
			switch (originalCon) {
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
				this._originalConWeightReduction = 0;
				break;

			default:// 9 UP
				this._originalConWeightReduction = 1;
				break;
			}

		} else if (this.isWizard()) {
			switch (originalCon) {
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
				this._originalConWeightReduction = 0;
				break;

			case 13:
			case 14:
				this._originalConWeightReduction = 1;
				break;

			default:// 15 UP
				this._originalConWeightReduction = 2;
				break;
			}

		} else if (this.isDragonKnight()) {
			this._originalConWeightReduction = 0;

		} else if (this.isIllusionist()) {
			switch (originalCon) {
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
			case 15:
			case 16:
				this._originalConWeightReduction = 0;
				break;

			case 17:
				this._originalConWeightReduction = 1;
				break;

			default:// 18 UP
				this._originalConWeightReduction = 2;
				break;
			}
		}
	}

	private final ArrayList<String> _excludeList = new ArrayList<String>();

	public void addExclude(final String name) {
		_excludeList.add(name);
	}

	/**
	 * ???????????????????????????????????????????????????????????????????????????
	 * 
	 * @param name
	 *            ??????????????????????????????
	 * @return ????????????????????????????????????????????????????????????????????????????????????????????? ??????????????????????????????????????????????????????????????????null????????????
	 */
	public String removeExclude(final String name) {
		for (final String each : _excludeList) {
			if (each.equalsIgnoreCase(name)) {
				_excludeList.remove(each);
				return each;
			}
		}
		return null;
	}

	/**
	 * ??????????????????????????????????????????????????????????????????true?????????
	 */
	public boolean excludes(final String name) {
		for (final String each : _excludeList) {
			if (each.equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * ???????????????????????????16?????????????????????????????????
	 */
	public boolean isExcludeListFull() {
		return (_excludeList.size() >= 16) ? true : false;
	}

	// -- ????????????????????? --
	private final AcceleratorChecker _acceleratorChecker = new AcceleratorChecker(
			this);

	public AcceleratorChecker getAcceleratorChecker() {
		return _acceleratorChecker;
	}

	// ???????????????
	private AcceleratorChecker _speed = null;

	/**
	 * ???????????????
	 * 
	 * @return
	 */
	public AcceleratorChecker speed_Attack() {
		return _speed;
	}

	/** ??????????????????????????????(????????????). */
	private short tempBookmarkMapID;
	/** ??????????????????X??????(????????????). */
	private int tempBookmarkLocX;
	/** ??????????????????Y??????(????????????). */
	private int tempBookmarkLocY;

	/**
	 * ????????????????????????????????????(????????????).
	 * 
	 * @return tempBookmarkMapID
	 */
	public short getTempBookmarkMapID() {
		return tempBookmarkMapID;
	}

	/**
	 * ????????????????????????????????????(????????????).
	 * 
	 * @param tempBookmarkMapID
	 *            - tempBookmarkMapID
	 */
	public void setTempBookmarkMapID(short tempBookmarkMapID) {
		this.tempBookmarkMapID = tempBookmarkMapID;
	}

	/**
	 * ????????????????????????X??????(????????????).
	 * 
	 * @return tempBookmarkLocX
	 */
	public int getTempBookmarkLocX() {
		return tempBookmarkLocX;
	}

	/**
	 * ????????????????????????X??????(????????????).
	 * 
	 * @param tempBookmarkLocX
	 *            - tempBookmarkLocX
	 */
	public void setTempBookmarkLocX(int tempBookmarkLocX) {
		this.tempBookmarkLocX = tempBookmarkLocX;
	}

	/**
	 * ????????????????????????Y??????(????????????).
	 * 
	 * @return tempBookmarkLocY
	 */
	public int getTempBookmarkLocY() {
		return tempBookmarkLocY;
	}

	/**
	 * ????????????????????????Y??????(????????????).
	 * 
	 * @param tempBookmarkLocY
	 *            - tempBookmarkLocY
	 */
	public void setTempBookmarkLocY(int tempBookmarkLocY) {
		this.tempBookmarkLocY = tempBookmarkLocY;
	}

	private boolean _isPring;

	public void setPring(final boolean flg) {
		_isPring = flg;
	}

	public boolean isPring() {
		return _isPring;
	}

	private int _warid = 0;

	public void setWarid(final int id) {
		_warid = 0;
	}

	public int getWarid() {
		return _warid;
	}

	private int _membera = -1;

	public void setMembera(final int a) {
		_membera = a;
	}

	public int getMembera() {
		return _membera;
	}

	private int _memberb = -1;

	public void setMemberb(final int b) {
		_memberb = b;
	}

	public int getMemberb() {
		return _memberb;
	}

	private int _AItime;

	public void setAItime(final int time) {
		_AItime = time;
	}

	public int getAItime() {
		return _AItime;
	}

	private int _waittime;

	public void setWaittime(final int time) {
		_waittime = time;
	}

	public int getWaittime() {
		return _waittime;
	}

	private int _sum = -1;

	public void setSum(final int sum) {
		_sum = sum;
	}

	public int getSum() {
		return _sum;
	}

	private long _tempexp;

	public void setTempMaxExp(final long exp) {
		_tempexp = exp;
	}

	public long getTempMaxExp() {
		return _tempexp;
	}

	private int _tempLevel = 1;// ????????????????????????(??????)

	/**
	 * ????????????????????????(??????)
	 * 
	 * @return
	 */
	public int getTempLevel() {
		return this._tempLevel;
	}

	/**
	 * ????????????????????????(??????)
	 * 
	 * @param i
	 */
	public void setTempLevel(final int i) {
		this._tempLevel = i;
	}

	private boolean _isInCharReset = false;// ????????????????????????

	/**
	 * ??????????????????????????????
	 * 
	 * @return
	 */
	public boolean isInCharReset() {
		return this._isInCharReset;
	}

	/**
	 * ??????????????????????????????
	 * 
	 * @param flag
	 */
	public void setInCharReset(final boolean flag) {
		this._isInCharReset = flag;
	}

	private int _tempMaxLevel = 1;// ????????????????????????(??????)

	/**
	 * ????????????????????????(??????)
	 * 
	 * @return
	 */
	public int getTempMaxLevel() {
		return this._tempMaxLevel;
	}

	/**
	 * ????????????????????????(??????)
	 * 
	 * @param i
	 */
	public void setTempMaxLevel(final int i) {
		this._tempMaxLevel = i;
	}

	private byte _chatCount = 0;// ??????????????????

	private long _oldChatTimeInMillis = 0L;// ?????????????????????

	/**
	 * ????????????(?????????)
	 */
	public void checkChatInterval() {
		final long nowChatTimeInMillis = System.currentTimeMillis();
		if (this._chatCount == 0) {
			this._chatCount++;
			this._oldChatTimeInMillis = nowChatTimeInMillis;
			return;
		}

		final long chatInterval = nowChatTimeInMillis
				- this._oldChatTimeInMillis;
		// ????????????2?????????
		if (chatInterval > 2000) {
			this._chatCount = 0;
			this._oldChatTimeInMillis = 0;

		} else {
			if (this._chatCount >= 3) {
				this.setSkillEffect(L1SkillId.STATUS_POISON_SILENCE, 120 * 1000);
				this.sendPackets(new S_PacketBox(S_PacketBox.ICON_CHATBAN, 120));
				// \f3????????????????????????2???????????????????????????
				this.sendPackets(new S_ServerMessage(153));
				this._chatCount = 0;
				this._oldChatTimeInMillis = 0;
			}
			this._chatCount++;
		}
	}

	private byte _WhisperchatCount = 0;// ??????????????????

	private long _WhisperoldChatTimeInMillis = 0L;// ?????????????????????

	/**
	 * ????????????(?????????)
	 */
	public void checkWhisperChatInterval() {
		final long nowChatTimeInMillis = System.currentTimeMillis();
		if (this._WhisperchatCount == 0) {
			this._WhisperchatCount++;
			this._WhisperoldChatTimeInMillis = nowChatTimeInMillis;
			return;
		}

		final long chatInterval = nowChatTimeInMillis
				- this._WhisperoldChatTimeInMillis;
		// ????????????2?????????
		if (chatInterval > 2000) {
			this._WhisperchatCount = 0;
			this._WhisperoldChatTimeInMillis = 0;

		} else {
			if (this._WhisperchatCount >= 3) {
				this.setSkillEffect(L1SkillId.STATUS_POISON_SILENCE, 120 * 1000);
				this.sendPackets(new S_PacketBox(S_PacketBox.ICON_CHATBAN, 120));
				// \f3????????????????????????2???????????????????????????
				this.sendPackets(new S_ServerMessage(153));
				this._WhisperchatCount = 0;
				this._WhisperoldChatTimeInMillis = 0;
			}
			this._WhisperchatCount++;
		}
	}

	private boolean _ispk;

	public void setPK(final boolean flg) {
		_ispk = flg;
	}

	public boolean isPK() {
		return _ispk;
	}

	private boolean _isShowWorldChat = true;// ????????????(??????)

	/**
	 * ????????????(??????)
	 * 
	 * @return flag true:?????? false:??????
	 */
	public boolean isShowWorldChat() {
		return this._isShowWorldChat;
	}

	/**
	 * ????????????(??????)
	 * 
	 * @param flag
	 *            flag true:?????? false:??????
	 */
	public void setShowWorldChat(final boolean flag) {
		this._isShowWorldChat = flag;
	}

	private boolean _isCanWhisper = true;// ????????????(??????)

	/**
	 * ????????????(??????)
	 * 
	 * @return flag true:?????? false:??????
	 */
	public boolean isCanWhisper() {
		return this._isCanWhisper;
	}

	/**
	 * ????????????(??????)
	 * 
	 * @param flag
	 *            flag true:?????? false:??????
	 */
	public void setCanWhisper(final boolean flag) {
		this._isCanWhisper = flag;
	}

	// TODO ??????????????????

	/**
	 * ????????????
	 * 
	 * @param key
	 *            ??????<BR>
	 *            0 ????????????/??????????????? ???????????????<BR>
	 * 
	 *            1 ?????? (??????)<BR>
	 *            2 ?????? (??????)<BR>
	 *            3 ?????? (??????)<BR>
	 *            4 ?????? (??????)<BR>
	 *            5 ?????? (??????)<BR>
	 *            6 ?????? (??????)<BR>
	 * 
	 *            7 ?????? +-<BR>
	 *            8 ?????? +-<BR>
	 *            9 ?????? +-<BR>
	 *            10 ?????? +-<BR>
	 *            11 ?????? +-<BR>
	 *            12 ?????? +-<BR>
	 * 
	 *            13 ???????????????????????? 0:???????????? 1:???????????????<BR>
	 * @param value
	 *            ??????????????????
	 */
	public void add_levelList(final int key, final int value) {
		_uplevelList.put(key, value);
	}

	/**
	 * ??????????????????
	 * 
	 * @return
	 */
	public Map<Integer, Integer> get_uplevelList() {
		return this._uplevelList;
	}

	/**
	 * ??????????????????
	 * 
	 * @param key
	 * @return
	 */
	public Integer get_uplevelList(final int key) {
		return this._uplevelList.get(key);
	}

	/**
	 * ??????????????????????????????
	 */
	public void clear_uplevelList() {
		this._uplevelList.clear();
	}

	private int[] _is;

	/**
	 * ??????????????????????????????
	 * 
	 * @param is
	 */
	public void set_newPcOriginal(final int[] is) {
		this._is = is;
	}

	/**
	 * ????????????????????????????????????
	 * 
	 * @return
	 */
	public int[] get_newPcOriginal() {
		return this._is;
	}

	private int _rname = 0;// ????????????

	/**
	 * ????????????
	 * 
	 * @param
	 */
	public void rename(final int item) {
		_rname = item;
	}

	/**
	 * ????????????
	 * 
	 * @return
	 */
	public int is_rname() {
		return _rname;
	}

	private boolean _CanTradChat = true;

	public void setShowTradeChat(final boolean b) {
		_CanTradChat = b;
	}

	public boolean isCanTradeChat() {
		return this._CanTradChat;
	}

	// ??????
	public L1GamSpList getGamSplist() {
		return _gamSpList;
	}

	private int _clanRank; // ??? ????????????????????????(?????????????????????????????????????????????)

	public int getClanRank() {
		return _clanRank;
	}

	public void setClanRank(final int i) {
		_clanRank = i;
	}

	private int _pinksec = 0;

	public void setPinkSec(final int sec) {
		_pinksec = sec;
	}

	public int getPinkSec() {
		return _pinksec;
	}

	private Timestamp _deleteTime; // ???????????????????????????????????????

	public Timestamp getDeleteTime() {
		return this._deleteTime;
	}

	public void setDeleteTime(final Timestamp time) {
		this._deleteTime = time;
	}

	private int _old_lawful;

	/**
	 * ??????Lawful
	 * 
	 * @return
	 */
	public int getLawfulo() {
		return _old_lawful;
	}

	/**
	 * ??????Lawful
	 */
	public void onChangeLawful() {
		if (_old_lawful != getLawful()) {
			_old_lawful = getLawful();
			sendPackets(new S_Lawful(getId(), getLawful()));
		}
	}

	private int _deadsec = 600;

	public void setdeadsec(final int sec) {
		_deadsec = sec;
	}

	public int getdeadsec() {
		return _deadsec;
	}

	private double _itemexp = 1.0;

	public void setItemExp(final double exp) {
		_itemexp = exp;
	}

	public double getItemExp() {
		return _itemexp;
	}

	private boolean _checkfz = false;

	public void setCheckFZ(final boolean flg) {
		_checkfz = flg;
	}

	public boolean isCheckFZ() {
		return _checkfz;
	}

	private int _moncount = 0;

	public void setKillMonCount(final int i) {
		_moncount = i;
	}

	public int getKillMonCount() {
		return _moncount;
	}

	public void addKillMonCount(final int i) {
		_moncount += i;
		this.sendPackets(new S_OwnCharStatus(this));
	}

	private final L1ExcludingMailList _excludingMailList = new L1ExcludingMailList();

	public L1ExcludingMailList getExcludingMailList() {
		return _excludingMailList;
	}

	private boolean _checkds = false;

	public void setCheck(final boolean flg) {
		_checkds = flg;
	}

	public boolean isCheck() {
		return _checkds;
	}

	private String _bianshenString = "??????";

	public void setBianshen(final String bs) {
		_bianshenString = bs;
	}

	public String getBianshen() {
		return _bianshenString;
	}

	private boolean _showemblem;

	public void setShowEmblem(final boolean b) {
		_showemblem = b;
	}

	public boolean isShowEmblem() {
		return _showemblem;
	}

	private String _toukuiname = "";

	public void setTouKuiName(final String name) {
		_toukuiname = name;
	}

	public String getTouKuiName() {
		return _toukuiname;
	}

	private int _tuokui_objId = 0;

	public void set_tuokui_objId(final int eq_objId) {
		_tuokui_objId = eq_objId;
	}

	public int get_tuokui_objId() {
		return _tuokui_objId;
	}

	/*
	 * private final L1PCAction _action; public L1PCAction getAction() { return
	 * _action; }
	 */

	private int _ezpayCount = 0;

	public int getEzpayCount() {
		return _ezpayCount;
	}

	public void setEzpayCount(final int ezpayCount) {
		_ezpayCount = ezpayCount;
	}

	public void addEzpayCount(final int ezpayCount) {
		_ezpayCount += ezpayCount;
	}

	private boolean _healHPAI = false;
	private boolean _healAIProcess = false;

	public void startHealHPAI() {
		if (_healHPAI) {
			return;
		}
		if (_healAIProcess) {
			return;
		}
		_healHPAI = true;
		_healAIProcess = true;
		new L1PcHealAI(this).startAI();
	}

	public void setHealAI(final boolean healHPAI) {
		_healHPAI = healHPAI;
	}

	public boolean getHealHPAI() {
		return _healHPAI;
	}

	public boolean isHealAIProcess() {
		return _healAIProcess;
	}

	public void setHealAIProcess(final boolean AIProcess) {
		_healAIProcess = AIProcess;
	}

	private final List<Integer> _healHpPotionList = new ArrayList<Integer>();

	public void addHealHpPotion(final int itemId) {
		_healHpPotionList.add(itemId);
	}

	public void clearHealHpPotion() {
		_healHpPotionList.clear();
	}

	public List<Integer> getHealHpPotionList() {
		return _healHpPotionList;
	}

	private final int[] _weaponObjIdList = new int[18];

	public void setWeaponItemObjId(final int itemObjId, final int index) {
		_weaponObjIdList[index] = itemObjId;
	}

	public int getWeaponItemObjId(final int index) {
		return _weaponObjIdList[index];
	}

	public int[] getWeaponItemList() {
		return _weaponObjIdList;
	}

	private final int[] _selHealHpPotion = new int[4];

	public void setSelHealHpPotion(final int itemId, final int healHp,
			final int gfxid) {
		if (_selHealHpPotion[0] != itemId) {
			final L1Item item = ItemTable.getInstance().getTemplate(itemId);
			if (item != null) {
				_selHealHpPotion[0] = itemId;
				_selHealHpPotion[1] = healHp;
				_selHealHpPotion[2] = gfxid;
				_selHealHpPotion[3] = item.get_delaytime();
				if (getHealHPAI()) {
					sendPackets(new S_SystemMessage("????????????????????????" + item.getName()));
				}
			}
		}
	}

	public int[] getSelHealHpPotion() {
		return _selHealHpPotion;
	}

	private int _healpersenthp = 20;

	public void setHealpersentHp(final int healpersenthp) {
		if (healpersenthp > 90) {
			_healpersenthp = 90;
		} else {
			_healpersenthp = healpersenthp;
		}
	}

	public int getHealpersentHP() {
		return _healpersenthp;
	}

	private int _Deathcount = 0;

	public int get_Deathcount() {
		return this._Deathcount;
	}

	public void add_Deathcount(final int detahcount) {
		this._Deathcount += detahcount;
	}

	public void set_Deathcount(final int detahcount) {
		this._Deathcount = detahcount;
	}

	private int _clanteleteId = 0;

	public void setClanTeletePcId(final int id) {
		_clanteleteId = id;
	}

	public int getClanTeletePcId() {
		return _clanteleteId;
	}

	private ArrayList<L1Spawn> _spawnBossList = new ArrayList<L1Spawn>();

	public void addSpawnBossItem(L1Spawn spawn) {
		_spawnBossList.add(spawn);
	}

	public void clearSpawnBossList() {
		_spawnBossList.clear();
	}

	public ArrayList<L1Spawn> getSpawnBossList() {
		return _spawnBossList;
	}

	private int _listpage = 0;

	public int getPage() {
		return _listpage;
	}

	public void addPage(final int page) {
		_listpage += page;
	}

	public void setPage(final int page) {
		_listpage = page;
	}

	private int _hitModifierByArmor = 0;

	public int getHitModifierByArmor() {
		return _hitModifierByArmor;
	}

	public void addHitModifierByArmor(int i) {
		_hitModifierByArmor += i;
	}

	private int _dmgModifierByArmor = 0;

	public int getDmgModifierByArmor() {
		return _dmgModifierByArmor;
	}

	public void addDmgModifierByArmor(int i) {
		_dmgModifierByArmor += i;
	}

	private int _bowHitModifierByArmor = 0;

	public int getBowHitModifierByArmor() {
		return _bowHitModifierByArmor;
	}

	public void addBowHitModifierByArmor(int i) {
		_bowHitModifierByArmor += i;
	}

	private int _bowDmgModifierByArmor = 0;

	public int getBowDmgModifierByArmor() {
		return _bowDmgModifierByArmor;
	}

	public void addBowDmgModifierByArmor(int i) {
		_bowDmgModifierByArmor += i;
	}

	private long _guajiAITime = 0;

	public long getGuaJiAITime() {
		return _guajiAITime;
	}

	public void setGuaJiAITime(final long nowtime) {
		_guajiAITime = nowtime;
	}

	private int _guajiAIattackcount = 0;

	public int getGuaJiAIAttackCount() {
		return _guajiAIattackcount;
	}

	public void setGuaJiAIAttackCount(final int count) {
		_guajiAIattackcount = count;
	}

	public void addGuaJiAIAttackCount(final int count) {
		_guajiAIattackcount += count;
	}

	private boolean _guaJiAI = true;

	public boolean getGuaJiAI() {
		return _guaJiAI;
	}

	public void setGuaJiAI(final boolean b) {
		_guaJiAI = b;
	}

	private int _dicezuobi = 0;

	public void setDiceZuoBi(final int dian) {
		_dicezuobi = dian;
	}

	public int getDiceZuoBi() {
		return _dicezuobi;
	}

	private int _oleLocx = 0;

	public void setOleLocX(final int oleLocx) {
		_oleLocx = oleLocx;
	}

	public int getOleLocX() {
		return _oleLocx;
	}

	private int _oleLocy = 0;

	public void setOleLocY(final int oleLocy) {
		_oleLocy = oleLocy;
	}

	public int getOleLocY() {
		return _oleLocy;
	}

	private boolean _bind = false;

	public boolean getBind() {
		return _bind;
	}

	public void setBind(final boolean bind) {
		_bind = bind;
	}

	/**
	 * ??????????????????
	 * 
	 * @return
	 */
	public boolean isElfBrave() {
		return this.hasSkillEffect(L1SkillId.STATUS_ELFBRAVE);
	}

	/** ??????????????????1. */
	private int equipmentRing1ID;
	/** ??????????????????2. */
	private int equipmentRing2ID;
	/** ??????????????????3. */
	private int equipmentRing3ID;
	/** ??????????????????4. */
	private int equipmentRing4ID;

	/**
	 * ????????????????????????1.
	 * 
	 * @return ????????????OBJID
	 */
	public int getEquipmentRing1ID() {
		return equipmentRing1ID;
	}

	/**
	 * ????????????????????????1.
	 * 
	 * @param i
	 *            - ????????????OBJID
	 */
	public void setEquipmentRing1ID(int i) {
		equipmentRing1ID = i;
	}

	/**
	 * ????????????????????????2.
	 * 
	 * @return ????????????OBJID
	 */
	public int getEquipmentRing2ID() {
		return equipmentRing2ID;
	}

	/**
	 * ????????????????????????2.
	 * 
	 * @param i
	 *            - ????????????OBJID
	 */
	public void setEquipmentRing2ID(int i) {
		equipmentRing2ID = i;
	}

	/**
	 * ????????????????????????3.
	 * 
	 * @return ????????????OBJID
	 */
	public int getEquipmentRing3ID() {
		return equipmentRing3ID;
	}

	/**
	 * ????????????????????????3.
	 * 
	 * @param i
	 *            - ????????????OBJID
	 */
	public void setEquipmentRing3ID(int i) {
		equipmentRing3ID = i;
	}

	/**
	 * ????????????????????????4.
	 * 
	 * @return ????????????OBJID
	 */
	public int getEquipmentRing4ID() {
		return equipmentRing4ID;
	}

	/**
	 * ????????????????????????4.
	 * 
	 * @param i
	 *            - ????????????OBJID
	 */
	public void setEquipmentRing4ID(int i) {
		equipmentRing4ID = i;
	}

	/** ?????????????????????????????????. */
	private boolean isLoginToServer;

	/**
	 * ?????????????????????????????????.
	 * 
	 * @return ?????? true or false
	 */
	public boolean isLoginToServer() {
		return isLoginToServer;
	}

	/**
	 * ??????????????????????????????.
	 * 
	 * @param flag
	 *            - true or false
	 */
	public void setLoginToServer(boolean flag) {
		isLoginToServer = flag;
	}

	// // ?????????????????????
	protected NpcMoveExecutor _pcMove = null;// XXX

	public void startAI() {
		if (this.isDead()) {
			return;
		}
		if (this.isGhost()) {
			return;
		}
		if (this.getCurrentHp() <= 0) {
			return;
		}
		if (this.isPrivateShop()) {
			return;
		}
		if (this.isParalyzed()) {
			return;
		}
		if (this.isAiRunning()) {
			return;
		}
		if (_pcMove == null) {
			_pcMove = new pcMove(this);
		}
		this.setActived(true);
		this.setAiRunning(true);
		final L1PcAI npcai = new L1PcAI(this);
		npcai.startAI();
	}

	public void clearMove() {
		if (_pcMove != null) {
			_pcMove.clear();
		}
	}

	private boolean _aiRunning = false;

	public void setAiRunning(final boolean b) {
		_aiRunning = b;
	}

	public boolean isAiRunning() {
		return this._aiRunning;
	}

	private boolean _isActived = false;

	public boolean isActived() {
		return _isActived;
	}

	public void setActived(final boolean b) {
		_isActived = b;
	}

	public void allTargetClear() {
//		_hateList.clear();
//		_AItarget = null;
//		setFirstAttack(false);
		 if (_pcMove != null) {
	            _pcMove.clear();
	        }
		 _AItarget = null;
	}

	private boolean _firstAttack = false;

	protected void setFirstAttack(final boolean firstAttack) {
		this._firstAttack = firstAttack;
	}

	protected boolean isFirstAttack() {
		return this._firstAttack;
	}

	private L1Character _AItarget = null;

	public final L1HateList _hateList = new L1HateList();// ????????????

	public void addHateList(final L1Character cha, final int hate) {
		_hateList.add(cha, hate);
	}

	/**
	 * ??????????????????
	 */
	public void checkTarget() {
		 try {
	            if (_AItarget == null) {// ????????????
	            	//targetClear();
	                return;
	            }
	            if (_AItarget.getMapId() != getMapId()) {// ?????????????????????
	            	targetClear();
	                return;
	            }
	            if (_AItarget.getCurrentHp() <= 0) {// ??????HP????????????0
	            	targetClear();
	                return;
	            }
	            if (_AItarget.isDead()) {// ????????????
	            	targetClear();
	                return;
	            }

	            final int distance = getLocation().getTileDistance(
	            		_AItarget.getLocation());
	            if (distance > 8) {
	            	targetClear();
	                return;
	            }

	        } catch (final Exception e) {
	            return;
	        }
	    }

	/**
	 * ??????????????????
	 */
	public void targetClear() {
		if (_AItarget == null) {
			return;
		}
		_AItarget = null;
	}

//	public void tagertClear() {
//		if (_AItarget == null) {
//			return;
//		}
//		if (_hateList.containsKey(_AItarget)) {
//			_hateList.remove(_AItarget);
//			// this.searchTarget();
//			setFirstAttack(false);
//		}
//		_AItarget = null;
//	}

	private boolean _Pathfinding = false; // ?????????.. hjx1000

	/**
	 * ???????????????????????????
	 * 
	 * @return
	 */
	public boolean isPathfinding() {
		return this._Pathfinding;
	}

	public void setPathfinding(final boolean fla) {
		this._Pathfinding = fla;
	}

	/**
	 * ????????????
	 */
	public L1Character is_now_target() {
		return _AItarget;
	}

	/**
	 * ????????????????????????
	 * 
	 * @param cha
	 */
	public void setNowTarget(final L1Character cha) {
		this._AItarget = cha;
	}

	/**
	 * ????????????
	 */
	public void searchTarget() {
	   	int hate = 8;
        final Collection<L1Object> allObj = L1World.getInstance()
                .getVisibleObjects(this, 8);
        for (final Iterator<L1Object> iter = allObj.iterator(); iter.hasNext();) {
            final L1Object obj = iter.next();
            if (!(obj instanceof L1MonsterInstance)) {
            	continue;
            }
            final L1MonsterInstance mob = (L1MonsterInstance) obj;
        	if (mob.isDead()) {
        		continue;
        	}
            if (mob.getCurrentHp() <= 0) {
                continue;
            }
            if (mob.getHiddenStatus() > 0) {
            	continue;                	
            }
            if (mob.getAtkspeed() == 0) {
            	continue;
            }
            if (mob.hasSkillEffect(this.getId() + 100000)
            		&& !this.isAttackPosition(mob.getX(), mob.getY(), 1)) {
            	continue;
            }
            if (mob != null) {
                final int Distance = this.getTileLineDistance(mob);	
                if (hate > Distance) {
                	_AItarget = mob;
                    hate = Distance;
                }
                if (hate < 2) {
                	break;
                }
            }
        }
		//if (isActived()) { // ?????????????????????

			// ??????????????????????????????????????????
			//if (this.getMap().isTeleportable()) { // ?????????????????????????????????
				// hjx1000
		Boolean isNoTarget = false;
        if (_AItarget == null && isNoTarget) { //?????????????????????
				if (this.getInventory().consumeItem(40308, 50)) {
					L1Teleport.randomTeleport(this, true);
				this.sendPackets(new S_SystemMessage(
						"????????????.?????????50??????!"));
			} else {
				// this.setskillAuto_gj(false);
				this.sendPackets(new S_SystemMessage(
						"????????????50????????????.????????????????????????????????????..."));
				this.setActived(false);
				final L1Location newLocation = new L1Location(33437, 32812, 4)
						.randomLocation(10, false);
				L1Teleport.teleport(this, newLocation.getX(),
						newLocation.getY(), (short) newLocation.getMapId(), 5,
						true);
				}
			}
		}
	

	private int _randomMoveDirection = 0;

	public int getrandomMoveDirection() {
		return _randomMoveDirection;
	}

	public void setrandomMoveDirection(int randomMoveDirection) {
		this._randomMoveDirection = randomMoveDirection;
	}

	/**
	 * ????????????????????? (????????????AI??????????????????)<BR>
	 * ???????????? ??????????????????
	 * 
	 * @return true:??????AI???????????? <BR>
	 *         false:??????AI???????????????
	 */
	public void noTarget() {
    	if (!_Pathfinding) {
        	_Pathfinding = true; //??????????????? 
    	}
    	if (_randomMoveDirection > 7) {
    		_randomMoveDirection = 0;
    	}
        //System.out.println("_randomMoveDirection=:" + _randomMoveDirection);
        if (_pcMove != null) {
            if (getrandomMoveDirection() < 8) {
                int dir = _pcMove
                        .checkObject(_randomMoveDirection);
                dir = _pcMove.openDoor(dir);

                if (dir != -1) {
                    _pcMove.setDirectionMove(dir);
                } else {
                	_randomMoveDirection = _random.nextInt(8);
                }
            }
        }
    }

	private L1Location _startGuaJiLoc = null;

	private L1Location getStartLoc() {
		return _startGuaJiLoc;
	}

	public void setStartGuaJiLoc(final L1Location loc) {
		_startGuaJiLoc = loc;
	}

	/**
	 * ????????????????????? (???????????????)
	 */
	public void onTarget() {
		try {
			final L1Character target = _AItarget;

			if (target == null) {
				return;
			}
			// if (target )
			attack(target);
			// System.out.println("?????????????????????" + target.getName());

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

//	private void attack(L1Character target) {
//		int attack_Range = 1;
//		if (this.getWeapon() != null) {
//			attack_Range = this.getWeapon().getItem().getRange();
//		}
//		if (attack_Range < 0) {
//			attack_Range = 13;
//		}
//		if (isAttackPosition(target.getX(), target.getY(), attack_Range)) {// ?????????????????????????????????
//			setHeading(targetDirection(target.getX(), target.getY()));
//			attackTarget(target);
//		} else { // ?????????????????????
//			int dir = _pcMove.moveDirection(target.getX(), target.getY());
//			if (dir == -1) {
//				if (!target.hasSkillEffect(this.getId() + 100000)) {
//					target.setSkillEffect(this.getId() + 100000, 30000);
//				}
//				tagertClear();
//				if (!_hateList.isEmpty()) {
//					_AItarget = _hateList.getMaxHateCharacter();
//					checkTarget();
//				}
//			} else {
//				_pcMove.setDirectionMove(dir);
//				setSleepTime(calcSleepTime(2));
//			}
//			// System.out.println("?????????????????????" + dir);
//		}
//	}
	
	 private void attack(L1Character target) {
	        // ??????????????????
	    	int attack_Range = 1;
	    	if (this.getWeapon() != null) {
	    		attack_Range = this.getWeapon().getItem().getRange();
	    	}
	    	if (attack_Range < 0) {
	    		attack_Range = 15;
	    	}
	        if (isAttackPosition(target.getX(), target.getY(), attack_Range)) {// ?????????????????????????????????
	            setHeading(targetDirection(target.getX(), target.getY()));
	            attackTarget(target);
	            this._Attack_or_walk = true;
	            // XXX
	            if (_pcMove != null) {
	                _pcMove.clear();
	            }

	        } else { // ?????????????????????
//	                final int distance = getLocation().getTileDistance(
//	                        target.getLocation());
	                if (_pcMove != null) {
	                    final int dir = _pcMove.moveDirection(target.getX(),
	                            target.getY());
	                    if (dir == -1) {
	                    	_AItarget.setSkillEffect(this.getId() + 100000, 20000);//??????20?????????
	                    	targetClear();

	                    } else {
	                        _pcMove.setDirectionMove(dir);
//	                        setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
	                        this._Attack_or_walk = false;
	                        
	                    }
	                }
	        }
	    }
	 
	    private boolean _Attack_or_walk = false;//??????????????? 
	    /**
	     * ??????????????????
	     * true = ??????
	     * false = ??????
	     */
	    public boolean Attack_or_walk() {
	    	return this._Attack_or_walk;
	    }
	  
	//
	private int _sleep_time = 1000;

	public void setSleepTime(final int sleep_time) {
		_sleep_time = sleep_time;
	}

	public int getSleepTime() {
		return _sleep_time;
	}

	//
	/**
	 * ?????????????????????
	 * 
	 * @param target
	 */
	public void attackTarget(final L1Character target) {
		// System.out.println("?????????????????????");

		if (this.getInventory().getWeight240() >= 197) { // ????????????
			// 110 \f1??????????????????????????????????????????
			this.sendPackets(new S_ServerMessage(110));
			// _log.error("??????????????????:????????????");
			return;
		}

		if (hasSkillEffect(L1SkillId.STATUS_CURSE_PARALYZED)) {
			return;
		}
		if (hasSkillEffect(L1SkillId.STATUS_POISON_PARALYZED)) {
			return;
		}
		if (hasSkillEffect(L1SkillId.STATUS_FREEZE)) {
			return;
		}
		if (target instanceof L1PcInstance) {
			final L1PcInstance player = (L1PcInstance) target;
			if (player.isTeleport()) { // ????????????????????????
				return;
			}
			if (!player.isPinkName()) {
				this.allTargetClear();
				return;
			}

		} else if (target instanceof L1PetInstance) {
			final L1PetInstance pet = (L1PetInstance) target;
			final L1Character cha = pet.getMaster();
			if (cha instanceof L1PcInstance) {
				final L1PcInstance player = (L1PcInstance) cha;
				if (player.isTeleport()) { // ????????????????????????
					return;
				}
			}

		} else if (target instanceof L1SummonInstance) {
			final L1SummonInstance summon = (L1SummonInstance) target;
			final L1Character cha = summon.getMaster();
			if (cha instanceof L1PcInstance) {
				final L1PcInstance player = (L1PcInstance) cha;
				if (player.isTeleport()) { // ????????????????????????
					return;
				}
			}
		}

		if (target instanceof L1NpcInstance) {
			final L1NpcInstance npc = (L1NpcInstance) target;
			if (npc.getHiddenStatus() != 0) { // ?????????????????????????????????????????????
				this.allTargetClear();
				return;
			}
		}
		
		 target.onAction(this);
		 
		 
		if (isGuaJiSkill()) {
			_old_skill_time = System.currentTimeMillis();
			final L1SkillUse skilluse = new L1SkillUse();
			skilluse.handleCommands(this, _selGuaJiSkillId, target.getId(),
					target.getX(), target.getY(), null, 0,
					L1SkillUse.TYPE_NORMAL);
			setSleepTime(calcSleepTime(3));
		} else {
			target.onAction(this);
			setSleepTime(calcSleepTime(1));
		}
	}

	private long _old_skill_time = 0;

	private boolean isGuaJiSkill() {
		if (_selGuaJiSkillId <= 0) {
			return false;
		}
		int time = 10000;
		if (_selGuaJiSkillId == 132) {
			if (this.getWeapon() == null) {
				return false;
			}
			if (this.getWeapon().getItem().getType1() != 20) {
				return false;
			}
			time = 3000;
		} else if (_selGuaJiSkillId == 38 || _selGuaJiSkillId == 46) {
			time = 2000;
		}
		final long nowskilltime = System.currentTimeMillis();
		if (nowskilltime - _old_skill_time <= time) {
			return false;
		}
		if (this.getCurrentHp() <= _selGuaJiSkillHP) {
			return false;
		}
		if (this.getCurrentMp() < _selGuaJiSkillMP) {
			return false;
		}
		return true;
	}

	private int calcSleepTime(final int type) {
		int interval = 0;
		switch (type) {
		case 1:
			interval = SprTable.get().getAttackSpeed(getTempCharGfx(),
					getCurrentWeapon() + 1);
			interval *= 1.05;
			break;
		case 2:
			interval = SprTable.get().getMoveSpeed(getTempCharGfx(),
					getCurrentWeapon());
			break;
		case 3:
			interval = SprTable.get().getDirSpellSpeed(getTempCharGfx());
			interval *= 1.05;
			break;
		default:
			return 0;
		}
		final int time_steep = intervalR(type, interval);
		return time_steep < 100 ? 100 : time_steep;
	}

	private int intervalR(final int type, int interval) {
		try {
			if (isHaste()) {
				interval *= 0.755;// 0.755
			}

			if (type == 2 && isFastMovable()) {
				interval *= 0.755;// 0.665
			}

			if (isBrave()) {
				interval *= 0.755;// 0.755
			}

			if (isElfBrave()) {
				interval *= 0.855;// 0.855
			}

			if (type == 1 && isElfBrave()) {
				interval *= 0.9;// 0.9
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return interval;
	}

	//
	private int _pcAILoop = 0;

	public int getPcAILoop() {
		return _pcAILoop;
	}

	public void setPcAILoop(final int loop) {
		_pcAILoop = loop;
	}

	//
	private int _selGuaJiSkillHP = 0;
	private int _selGuaJiSkillMP = 0;

	private int _selGuaJiSkillId = 0;

	public int getSelGuaJiSkillId() {
		return _selGuaJiSkillId;
	}

	public void setSelGuaJiSkillId(final int selGuaJiSkillId) {
		if (selGuaJiSkillId == 0) {
			_selGuaJiSkillHP = 0;
			_selGuaJiSkillMP = 0;
			_selGuaJiSkillId = 0;
			return;
		}
		final L1Skills skill = SkillsTable.getInstance().getTemplate(
				selGuaJiSkillId);
		if (skill != null) {
			if (CharSkillReading.get()
					.spellCheck(this.getId(), selGuaJiSkillId)) {
				_selGuaJiSkillHP = skill.getHpConsume();
				_selGuaJiSkillMP = skill.getMpConsume();
				_selGuaJiSkillId = selGuaJiSkillId;
			} else {
				_selGuaJiSkillHP = 0;
				_selGuaJiSkillMP = 0;
				_selGuaJiSkillId = 0;
			}
		} else {
			_selGuaJiSkillHP = 0;
			_selGuaJiSkillMP = 0;
			_selGuaJiSkillId = 0;
		}
	}

	private int _selGuaJiRange = 0;

	public int getSelGuaJiRange() {
		return _selGuaJiRange;
	}

	public void setSelGuaJiRange(final int selGuaJiRange) {
		_selGuaJiRange = selGuaJiRange;
	}

	public void addSelGuaJiRange(final int selGuaJiRange) {
		_selGuaJiRange += selGuaJiRange;
		if (_selGuaJiRange < 0) {
			_selGuaJiRange = 0;
		}
	}

	@Override
	public int getSp() {
		return super.getSp() + getSpReductionByClan(this);
	}

	@Override
	public int getDmgup() {
		return super.getDmgup() + getDmgReductionByClan(this);
	}

	@Override
	public int getBowDmgup() {
		return super.getBowDmgup() + getDmgReductionByClan(this);
	}

	/**
	 * ????????????????????????
	 * 
	 * @return
	 */
	private int getDmgReductionByClan(final L1PcInstance pc) {
		int dmg = 0;
		try {
			if (pc == null) {
				return 0;
			}
			final L1Clan clan = pc.getClan();
			if (pc.getClanid() == 0 || clan == null) {
				return 0;
			}
			// ??????????????????
			if (clan.isClanskill()) {
				if (clan.getSkillLevel() == 1) {
					dmg += 1;
				} else if (clan.getSkillLevel() == 2) {
					dmg += 2;
				} else if (clan.getSkillLevel() == 3) {
					dmg += 3;
				} else if (clan.getSkillLevel() == 4) {
					dmg += 4;
				} else if (clan.getSkillLevel() == 5) {
					dmg += 5;
				}
			}

		} catch (final Exception e) {
			return 0;
		}
		return dmg;
	}

	/**
	 * ????????????sp??????
	 * 
	 * @return
	 */
	private int getSpReductionByClan(final L1PcInstance pc) {
		int sp = 0;
		try {
			if (pc == null) {
				return 0;
			}
			if (pc.getClanid() == 0) {
				return 0;
			}
			final L1Clan clan = pc.getClan();
			if (clan == null) {
				return 0;
			}
			// ??????????????????
			if (clan.isClanskill()) {
				if (clan.getSkillLevel() == 1) {
					sp += 1;
				} else if (clan.getSkillLevel() == 2) {
					sp += 2;
				}
			}
		} catch (final Exception e) {
			return 0;
		}
		return sp;
	}

	private long _adenaTradeCount = 0;

	/**
	 * ???????????? ???????????????????????????
	 * 
	 * @param adenaTradeCount
	 */
	public void setAdenaTradeCount(final long adenaTradeCount) {
		_adenaTradeCount = adenaTradeCount;
	}

	/**
	 * ???????????? ???????????????????????????
	 * 
	 * @return
	 */
	public long getAdenaTradeCount() {
		return _adenaTradeCount;
	}

	// ???????????? ???????????????????????????
	private long _adenaTradeAmount = 0;

	/**
	 * ???????????? ???????????????????????????
	 * 
	 * @param adenaTradeAmount
	 */
	public void setAdenaTradeAmount(final long adenaTradeAmount) {
		_adenaTradeAmount = adenaTradeAmount;
	}

	/**
	 * ???????????? ???????????????????????????
	 * 
	 * @return
	 */
	public long getAdenaTradeAmount() {
		return _adenaTradeAmount;
	}

	private List<Integer> _adenaTradeIndexList = new CopyOnWriteArrayList<Integer>();

	public void addAdenaTradeIndex(final int id) {
		_adenaTradeIndexList.add(id);
	}

	public void clearAdenaTradeIndexList() {
		_adenaTradeIndexList.clear();
	}

	public List<Integer> getAdenaTradeIndexList() {
		return _adenaTradeIndexList;
	}

	private List<L1CharacterAdenaTrade> _adenaTradeList = new CopyOnWriteArrayList<L1CharacterAdenaTrade>();

	public void addAdenaTradeItem(final L1CharacterAdenaTrade adenaTrade) {
		_adenaTradeList.add(adenaTrade);
	}

	public void clearAdenaTradeList() {
		_adenaTradeList.clear();
	}

	public List<L1CharacterAdenaTrade> getAdenaTradeList() {
		return _adenaTradeList;
	}

	private int _adenaTradeId = 0;

	/**
	 * ???????????? ????????????????????????
	 * 
	 * @param adenaTradeId
	 */
	public void setAdenaTradeId(final int adenaTradeId) {
		_adenaTradeId = adenaTradeId;
	}

	/**
	 * ???????????? ????????????????????????
	 * 
	 * @return
	 */
	public int getAdenaTradeId() {
		return _adenaTradeId;
	}

	private final List<L1FindShopSell> _findsellList = new ArrayList<L1FindShopSell>();

	public List<L1FindShopSell> getFindSellList() {
		return _findsellList;
	}

	public int getFindSellListSize() {
		return _findsellList.size();
	}

	public void clearFindSellList() {
		_findsellList.clear();
	}

	private boolean _showHealMessage = true;

	public boolean IsShowHealMessage() {
		return _showHealMessage;
	}

	public void setShowHealMessage(final boolean showhealmessage) {
		_showHealMessage = showhealmessage;
	}

	private L1BiaoCheInstance _biaCheInstance = null;

	public void setBiaoChe(L1BiaoCheInstance biaocheInstance) {
		_biaCheInstance = biaocheInstance;
	}

	public L1BiaoCheInstance getBiaoChe() {
		return _biaCheInstance;
	}

	private int _jiequbiaochecount = 0;

	public int getJieQuBiaoCheCount() {

		return _jiequbiaochecount;
	}

	public void addJieQuBiaoCheCount(int i) {
		_jiequbiaochecount += i;
	}

	public void setJieQuBiaoCheCount(int i) {
		_jiequbiaochecount = i;
	}

	private short _tempBiaoCheMapId = 0;

	public void setTempBiaoCheMapId(short mapId) {
		_tempBiaoCheMapId = mapId;
	}

	public short getTempBiaoCheMapId() {
		return _tempBiaoCheMapId;
	}

	private int _tempBiaoCheLocX = 0;

	public void setTempBiaoCheLocX(int x) {
		_tempBiaoCheLocX = x;
	}

	public int getTempBiaoCheLocX() {
		return _tempBiaoCheLocX;
	}

	private int _tempBiaoCheLocY = 0;

	public void setTempBiaoCheLocY(int y) {
		_tempBiaoCheLocY = y;
	}

	public int getTempBiaoCheLocY() {
		return _tempBiaoCheLocY;
	}

	private int _dollfailcount = 0;

	public int getDollFailCount() {
		return _dollfailcount;
	}

	public void setDollFailCount(final int dollfailcount) {
		_dollfailcount = dollfailcount;
	}

	public void addDollFailCount(final int dollfailcount) {
		_dollfailcount += dollfailcount;
	}

	public void saveDollFailCount() {
		if (isGhost()) {
			return;
		}
		CharacterTable.getInstance().storeCharacterDollFailCount(this);
	}

	private boolean _checktwopassword = false;

	public void setCheckTwopassword(final boolean checktwopassword) {
		_checktwopassword = checktwopassword;
	}

	/**
	 * ??????????????????????????????
	 * 
	 * @return
	 */
	public boolean isCheckTwopassword() {
		return _checktwopassword;
	}

	private boolean _xiugaitwopassword = false;

	public void setXiuGaiTwopassword(final boolean xiugaitwopassword) {
		_xiugaitwopassword = xiugaitwopassword;
	}

	/**
	 * ??????????????????????????????
	 * 
	 * @return
	 */
	public boolean isXiuGaiTwopassword() {
		return _xiugaitwopassword;
	}

	private int _old_twopassword = -256;

	public void setOldTwoPassword(final int old_twopassword) {
		_old_twopassword = old_twopassword;
	}

	public int getOldTwoPassword() {
		return _old_twopassword;
	}

	private boolean _isShowEnchantMessage = true;

	public boolean isShowEnchantMessage() {
		return _isShowEnchantMessage;
	}

	public void setShowEnchantMessage(final boolean showenchantMessage) {
		_isShowEnchantMessage = showenchantMessage;
	}

	private long _h_time;// ??????????????????

	/**
	 * ??????????????????
	 * 
	 * @return
	 */
	public long get_h_time() {
		return _h_time;
	}

	/**
	 * ??????????????????
	 * 
	 * @param
	 */
	public void set_h_time(long time) {
		_h_time = time;
	}

	public void startRenameThread() {
		GeneralThreadPool.getInstance().execute((new RenameThread()));
	}

	class RenameThread extends Thread {
		@Override
		public void run() {
			for (int i = 10; i > 0; i--) {
				if (getOnlineStatus() == 0) {
					break;
				}
				sendPackets(new S_SystemMessage(String.format(
						"\\F2????????????%d???????????????????????????????????????.", i)));
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (getNetConnection() != null) {
				getNetConnection().kick();
			}
		}
	}

	private int _damageReductionByDoll = 0;

	public int getDamageReductionByDoll() {
		return _damageReductionByDoll;
	}

	public void addDamageReductionByDoll(final int i, final int r) {
		_damageReductionByDoll += i;
		_damageReductionrandomByDoll += r;
	}

	private int _damageReductionrandomByDoll = 0;

	public int getDamageReductionRandomByDoll() {
		return _damageReductionrandomByDoll;
	}

	private double _weightUPByDoll = 1.0D;// ????????????%

	/**
	 * ????????????%
	 * 
	 * @return
	 */
	public double get_weightUPByDoll() {
		return _weightUPByDoll;
	}

	/**
	 * ????????????%
	 * 
	 * @param i
	 */
	public void add_weightUPByDoll(final int i) {
		_weightUPByDoll += (i / 100D);
	}

	private int _byDollDmgupRandom = 0;

	public void addByDollDmgUpRandom(final int int1) {
		_byDollDmgupRandom += int1;
	}

	public int getByDollDmgUpRandom() {
		return _byDollDmgupRandom;
	}

	private int _byDollDmgupR = 0;

	public void addByDollDmgUpR(final int int2) {
		_byDollDmgupR += int2;
	}

	public int getByDollDmgUpR() {
		return _byDollDmgupR;
	}

	private int _byDollBowDmgupRandom = 0;

	public void addByDollBowDmgUpRandom(final int int1) {
		_byDollBowDmgupRandom += int1;
	}

	public int getByDollBowDmgUpRandom() {
		return _byDollBowDmgupRandom;
	}

	private int _byDollBowDmgupR = 0;

	public void addByDollBowDmgUpR(final int int2) {
		_byDollBowDmgupR += int2;
	}

	public int getByDollBowDmgUpR() {
		return _byDollBowDmgupR;
	}

	private double _expByDoll = 1.0;

	public void addExpByDoll(final int int2) {
		_expByDoll += (int2 / 100D);
	}

	public double getExpByDoll() {
		return _expByDoll;
	}

	private int _evasion;// ????????????(1/1000)

	/**
	 * ????????????
	 * 
	 * @param int1
	 */
	public void add_evasion(int int1) {
		_evasion += int1;
	}

	/**
	 * ??????????????????
	 * 
	 * @return
	 */
	public int get_evasion() {
		return _evasion;
	}

	private int _heallingPotion = 0;

	public void addHeallingPotion(final int heallingPotion) {
		_heallingPotion += heallingPotion;
	}

	public int getHeallingPotion() {
		return _heallingPotion;
	}

	private boolean _isMassTeleport = true;

	public boolean isMassTeleport() {
		return _isMassTeleport;
	}

	public void setMassTeleport(final boolean isMassTeleport) {
		_isMassTeleport = isMassTeleport;
	}

	// ??????????????????
	private boolean _ismakeitem = false;

	/**
	 * ??????????????????????????????
	 */
	public boolean ismakeitem() {
		return _ismakeitem;
	}

	/**
	 * ????????????????????????
	 */
	public void setismakeitem(boolean i) {
		_ismakeitem = i;
	}

	private boolean _iscangku = false;

	/**
	 * ??????????????????????????????
	 */
	public boolean iscangku() {
		return _iscangku;
	}

	/**
	 * ????????????????????????
	 */
	public void setcangku(boolean i) {
		_iscangku = i;
	}

	/**
	 * ??????????????????
	 */
	private boolean _isshunfei = false;

	/**
	 * ??????????????????
	 */
	public void set_isshunfei(final boolean b) {
		_isshunfei = b;
	}

	/**
	 * ??????????????????
	 */
	public boolean isshunfei() {
		return this._isshunfei;
	}

	/**
	 * ????????????
	 */
	private boolean _skillAuto = false;

	/**
	 * ????????????
	 */
	public boolean isskillAuto() {
		return this._skillAuto;
	}

	/**
	 * ????????????
	 */
	private static final int skillIds[] = new int[] { 26, 42, 43, 48, 79, 151,
			158, 148, 115, 117 };

	public void setskillAuto(final boolean setskillAuto) {
		this._skillAuto = setskillAuto;
		if (setskillAuto) {
			AutoMagic.automagic(this, 0);
		}
	}

	// /**
	// * ????????????
	// */
	// private static final int skillIds_gj[] = new int[] { 26, 42, 43, 48, 79,
	// 151,
	// 158, 148, 115, 117 };
	//
	// public void setskillAuto_gj(final boolean setskillAuto_gj) {
	// this._skillAuto_gj = setskillAuto_gj;
	// if (setskillAuto_gj) {
	// AutoMagic_GJ.automagic(this, skillIds_gj);
	// }
	// }

	// /** ???????????? **/
	// public final L1HateList _hateList = new L1HateList();// ????????????
	// private boolean _firstAttack = false;
	// protected NpcMoveExecutor _pcMove = null;// XXX
	// private L1Character _target = null;
	//
	//
	// /**
	// * ????????????????????????
	// *
	// * @param target
	// */
	// public void setNowTarget(final L1Character target) {
	// this._target = target;
	// }
	//
	// /**
	// * ????????????????????????
	// */
	// public L1Character getNowTarget() {
	// return this._target;
	// }
	//
	// /**
	// * ??????PC AI
	// */
	// public synchronized void startAI() {
	// if (this.isDead()) {
	// return;
	// }
	// if (this.isGhost()) {
	// return;
	// }
	// if (this.getCurrentHp() <= 0) {
	// return;
	// }
	// if (this.isPrivateShop()) {
	// return;
	// }
	// if (this.isParalyzed()) {
	// return;
	// }
	//
	// if (_pcMove != null) {
	// _pcMove = null;
	// }
	// _pcMove = new pcMove(this);
	// this.setAiRunning(true);
	// this.setActived(true);
	// final PcAI npcai = new PcAI(this);
	// npcai.startAI();
	//
	// }
	//
	// public boolean _aiRunning = false; // PC AI????????? ????????????
	//
	// /**
	// * PC AI????????? ????????????
	// *
	// * @param aiRunning
	// */
	// public void setAiRunning(final boolean aiRunning) {
	// this._aiRunning = aiRunning;
	// }
	//
	//
	// /**
	// * PC AI????????? ????????????
	// *
	// * @return
	// */
	// public boolean isAiRunning() {
	// return this._aiRunning;
	// }
	//
	// /**
	// * ??????????????????
	// */
	// public void allTargetClear() {
	// // XXX
	// if (_pcMove != null) {
	// _pcMove.clear();
	// }
	// _hateList.clear();
	// _target = null;
	// setFirstAttack(false);
	// }
	//
	// /**
	// * ??????????????????
	// */
	// public void targetClear() {
	// if (_target == null) {
	// return;
	// }
	// _hateList.remove(_target);
	// _target = null;
	// }
	//
	// /**
	// * ??????????????????
	// */
	// public void checkTarget() {
	// try {
	// if (_target == null) {// ????????????
	// // targetClear();
	// return;
	// }
	// if (_target.getMapId() != getMapId()) {// ?????????????????????
	// targetClear();
	// return;
	// }
	// if (_target.getCurrentHp() <= 0) {// ??????HP????????????0
	// targetClear();
	// return;
	// }
	// if (_target.isDead()) {// ????????????
	// targetClear();
	// return;
	// }
	//
	//
	// if (!_hateList.containsKey(_target)) {// ?????????????????????????????????
	// targetClear();
	// return;
	// }
	//
	// final int distance = getLocation().getTileDistance(
	// _target.getLocation());
	// if (distance > 15) {
	// targetClear();
	// return;
	// }
	//
	// } catch (final Exception e) {
	// return;
	// }
	// }
	//
	// /**
	// * ????????????
	// */
	// public L1Character is_now_target() {
	// return _target;
	// }
	//
	// /**
	// * ?????????????????????
	// *
	// * @param target
	// */
	// public void attackTarget(final L1Character target) {
	//
	// if (this.getInventory().getWeight240() >= 197) { // ????????????
	// // 110 \f1??????????????????????????????????????????
	// this.sendPackets(new S_ServerMessage(110));
	// // _log.error("??????????????????:????????????");
	// return;
	// }
	//
	// if (target instanceof L1PcInstance) {
	// final L1PcInstance player = (L1PcInstance) target;
	// if (player.isTeleport()) { // ????????????????????????
	// return;
	// }
	// if (!player.isPinkName()) {
	// this.allTargetClear();
	// return;
	// }
	//
	// } else if (target instanceof L1PetInstance) {
	// final L1PetInstance pet = (L1PetInstance) target;
	// final L1Character cha = pet.getMaster();
	// if (cha instanceof L1PcInstance) {
	// final L1PcInstance player = (L1PcInstance) cha;
	// if (player.isTeleport()) { // ????????????????????????
	// return;
	// }
	// }
	//
	// } else if (target instanceof L1SummonInstance) {
	// final L1SummonInstance summon = (L1SummonInstance) target;
	// final L1Character cha = summon.getMaster();
	// if (cha instanceof L1PcInstance) {
	// final L1PcInstance player = (L1PcInstance) cha;
	// if (player.isTeleport()) { // ????????????????????????
	// return;
	// }
	// }
	// }
	//
	// if (target instanceof L1NpcInstance) {
	// final L1NpcInstance npc = (L1NpcInstance) target;
	// if (npc.getHiddenStatus() != 0) { // ?????????????????????????????????????????????
	// this.allTargetClear();
	// return;
	// }
	// }
	// target.onAction(this);
	// // long h_time1 = Calendar.getInstance().getTimeInMillis() / 1000;//
	// // ????????????
	// // this.sendPackets(new S_SystemMessage("???????????????" + h_time1));
	// }
	//
	// public void searchTarget() {
	// // ??????????????????
	// // System.out.println("AI??????44444");
	// // L1MonsterInstance targetPlayer = searchTarget(this);
	// // System.out.println("AI??????666==" + targetPlayer);
	// // if (targetPlayer != null) {
	// // _hateList.add(targetPlayer, 0);
	// // _target = targetPlayer;
	// //
	// // }
	// final Collection<L1Object> allObj =
	// L1World.getInstance().getVisibleObjects(this,
	// 15);
	// for (final Iterator<L1Object> iter = allObj.iterator(); iter.hasNext();)
	// {
	// final L1Object obj = iter.next();
	// if (!(obj instanceof L1MonsterInstance)) {
	// continue;
	// }
	// final L1MonsterInstance mob = (L1MonsterInstance) obj;
	// if (mob.isDead()) {
	// continue;
	// }
	// if (mob.getCurrentHp() <= 0) {
	// continue;
	// }
	// if (mob.getHiddenStatus() > 0) {
	// continue;
	// }
	// if (mob.getAtkspeed() == 0) {
	// continue;
	// }
	//
	// if (mob.hasSkillEffect(this.getId() + 100000)
	// && !this.isAttackPosition(mob.getX(), mob.getY(), 1)) {
	// continue;
	// }
	//
	// if (mob != null) {
	// final int Distance = 15 - this.getTileLineDistance(mob);
	// _hateList.add(mob, Distance);
	// }
	// }
	// _target = _hateList.getMaxHateCharacter();
	// if (_target == null) { // ?????????????????????
	//
	// // ??????????????????????????????????????????
	// if (this.getMap().isTeleportable()
	// && this.getInventory().consumeItem(40100, 1)) { // ?????????????????????????????????
	// // hjx1000
	// L1Teleport.randomTeleport(this, true);
	// }
	//
	// // if (this.getMap().isTeleportable()
	// // && this.getInventory().checkItem(40100)) {
	// // L1Teleport.randomTeleport(this, true);
	// // }
	//
	// }
	// allObj.clear();
	// }
	//
	// /**
	// * ????????????????????? (???????????????)
	// */
	// public void onTarget() {
	// try {
	//
	// final L1Character target = _target;
	//
	// if (target == null) {
	// return;
	// }
	// attack(target);
	//
	// } catch (final Exception e) {
	// _log.error(e.getLocalizedMessage(), e);
	// }
	// }
	//
	// private void attack(L1Character target) {
	// // ??????????????????
	// int attack_Range = 1;
	// if (this.getWeapon() != null) {
	// attack_Range = this.getWeapon().getItem().getRange();
	// }
	// if (attack_Range < 0) {
	// attack_Range = 15;
	// }
	// if (isAttackPosition(target.getX(), target.getY(), attack_Range)) {//
	// ?????????????????????????????????
	// setHeading(targetDirection(target.getX(), target.getY()));
	// attackTarget(target);
	// // XXX
	// if (_pcMove != null) {
	// _pcMove.clear();
	// }
	//
	// } else { // ?????????????????????
	// // final int distance = getLocation().getTileDistance(
	// // target.getLocation());
	// if (_pcMove != null) {
	// final int dir = _pcMove.moveDirection(target.getX(),
	// target.getY());
	// if (dir == -1) {
	// _target.setSkillEffect(this.getId() + 100000, 20000);// ??????20?????????
	// //System.out.println("===AI??????===");
	// targetClear();
	//
	// } else {
	// _pcMove.setDirectionMove(dir);
	// //setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
	// }
	// }
	// }
	// }
	//
	// private boolean _actived = false; // ????????????
	// private boolean _Pathfinding = false; // ?????????.. hjx1000

	/**
	 * // * PC???????????? // * // * @param actived // * true:?????? false:??? //
	 */
	// public void setActived(final boolean actived) {
	// this._actived = actived;
	// }
	//
	// /**
	// * PC????????????
	// *
	// * @return true:?????? false:???
	// */
	// public boolean isActived() {
	// return this._actived;
	// }
	//
	// protected void setFirstAttack(final boolean firstAttack) {
	// this._firstAttack = firstAttack;
	// }
	//
	// protected boolean isFirstAttack() {
	// return this._firstAttack;
	// }
	//
	// /**
	// * ??????????????????
	// *
	// * @param cha
	// * @param hate
	// */
	// public void setHate(final L1Character cha, int hate) {
	// try {
	// if ((cha != null) && /* (cha.getId() != getId()) */_target != null) {
	// if (!isFirstAttack() && (hate > 0)) {
	// // hate += getMaxHp() / 10; // ???????????????
	// setFirstAttack(true);
	// if (_pcMove != null) {
	// _pcMove.clear();// XXX
	// }
	// // System.out.println("isFirstAttack=" + isFirstAttack());
	// _hateList.add(cha, 5);
	// _target = _hateList.getMaxHateCharacter();
	// checkTarget();
	// }
	// }
	//
	// } catch (final Exception e) {
	// return;
	// }
	// }
	//
	// /**
	// * ???????????????????????????
	// *
	// * @return
	// */
	// public boolean isPathfinding() {
	// return this._Pathfinding;
	// }
	//
	// public void setPathfinding(final boolean fla) {
	// this._Pathfinding = fla;
	// }
	//
	// // ??????????????????
	// // private int _randomMoveDistance = 0;
	// // ??????????????????
	// private int _randomMoveDirection = 0;
	//
	// public int getrandomMoveDirection() {
	// return _randomMoveDirection;
	// }
	//
	// public void setrandomMoveDirection(int randomMoveDirection) {
	// this._randomMoveDirection = randomMoveDirection;
	// }
	//
	// /**
	// * ????????????????????? (????????????AI??????????????????)<BR>
	// * ???????????? ??????????????????
	// *
	// * @return true:??????AI???????????? <BR>
	// * false:??????AI???????????????
	// */
	// public void noTarget() {
	// if (!_Pathfinding) {
	// _Pathfinding = true; // ???????????????
	// }
	// if (_randomMoveDirection > 7) {
	// _randomMoveDirection = 0;
	// }
	// // System.out.println("_randomMoveDirection=:" + _randomMoveDirection);
	// if (_pcMove != null) {
	// if (getrandomMoveDirection() < 8) {
	// int dir = _pcMove.checkObject(_randomMoveDirection);
	// dir = _pcMove.openDoor(dir);
	//
	// if (dir != -1) {
	// _pcMove.setDirectionMove(dir);
	// } else {
	// _randomMoveDirection = _random.nextInt(8);
	// }
	// }
	// }
	// }
	//
	// /**
	// * ????????????/????????????/??????/???????????????
	// *
	// * @return true:????????? false:???
	// */
	// public boolean isParalyzedX() {
	// // ????????????
	// if (hasSkillEffect(ICE_LANCE)) {
	// return true;
	// }
	// // ????????????
	// if (hasSkillEffect(FREEZING_BLIZZARD)) {
	// return true;
	// }
	// // ????????????
	// if (hasSkillEffect(FREEZING_BREATH)) {
	// return true;
	// }
	// // ????????????
	// if (hasSkillEffect(EARTH_BIND)) {
	// return true;
	// }
	// // ????????????
	// if (hasSkillEffect(SHOCK_STUN)) {
	// return true;
	// }
	// // ????????????
	// if (hasSkillEffect(BONE_BREAK)) {
	// return true;
	// }
	// // ??????????????????
	// if (hasSkillEffect(CURSE_PARALYZE)) {
	// return true;
	// }
	// if (hasSkillEffect(STATUS_POISON_PARALYZED)) { // ???????????????????????? hjx1000
	// return true;
	// }
	//
	// return false;
	// }

	private int _homeX; // ???????????????x??????

	public int getHomeX() {
		return this._homeX;
	}

	public void setHomeX(final int i) {
		this._homeX = i;
	}

	private int _homeY; // ???????????????y??????

	public int getHomeY() {
		return this._homeY;
	}

	public void setHomeY(final int i) {
		this._homeY = i;
	}

	private int _hookrange; // ????????????

	public int gethookrange() {
		return _hookrange;
	}

	public void sethookrange(final int i) {
		this._hookrange = i;
	}

	/**
     * ????????????ID
     */
    private int _awakeSkillId = 0;
    
    /**
     * ??????????????????ID
     * 
     * @return
     */
    public int getAwakeSkillId() {
        return this._awakeSkillId;
    }
    
    /**
     * ??????????????????ID
     * 
     * @param i
     */
    public void setAwakeSkillId(final int i) {
        this._awakeSkillId = i;
    }
    
    private boolean _isFoeSlayer = false;

	/**
	 * ?????????????????????
	 * 
	 * @return
	 */
	public boolean isFoeSlayer() {
		return _isFoeSlayer;
	}

	/**
	 * ?????????????????????
	 */
	public void isFoeSlayer(boolean isFoeSlayer) {
		_isFoeSlayer = isFoeSlayer;
	}

	private int _weaknss = 0;
	private long _weaknss_t = 0;// ??????

	/**
	 * ??????????????????
	 * 
	 * @return
	 */
	public long get_weaknss_t() {
		return _weaknss_t;
	}

	/**
	 * ??????????????????
	 * 
	 * @return
	 */
	public int get_weaknss() {
		return _weaknss;
	}

	/**
	 * ??????????????????
	 * 
	 * @param lv
	 */
	public void set_weaknss(int lv, long t) {
		_weaknss = lv;
		_weaknss_t = t;
		switch (_weaknss) { // ??????????????????????????? hjx1000
		case 1:
			this.sendPackets(new S_PacketBoxDk(S_PacketBoxDk.LV1));
			break;
		case 2:
			this.sendPackets(new S_PacketBoxDk(S_PacketBoxDk.LV2));
			break;
		case 3:
			this.sendPackets(new S_PacketBoxDk(S_PacketBoxDk.LV3));
			break;

		}
	}
}
