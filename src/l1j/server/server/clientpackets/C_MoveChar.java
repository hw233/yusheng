/* This program is free software; you can redistribute it and/or modify
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
package l1j.server.server.clientpackets;

import static l1j.server.server.model.Instance.L1PcInstance.REGENSTATE_MOVE;
import l1j.server.AcceleratorChecker;
import l1j.server.Config;
import l1j.server.server.WriteLogTxt;
import l1j.server.server.datatables.MapsNotAllowedTable;
import l1j.server.server.datatables.PolyTable;
import l1j.server.server.mina.LineageClient;
import l1j.server.server.model.Dungeon;
import l1j.server.server.model.DungeonRandom;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.trap.L1WorldTraps;
import l1j.server.server.serverpackets.S_Lock;
import l1j.server.server.serverpackets.S_MoveCharPacket;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.world.L1World;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_MoveChar extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_MoveChar.class);

	private static final byte HEADING_TABLE_X[] = { 0, 1, 1, 1, 0, -1, -1, -1 };

	private static final byte HEADING_TABLE_Y[] = { -1, -1, 0, 1, 1, 1, 0, -1 };
	// ???????????????????????????
/*	private void sendMapTileLog(L1PcInstance pc) {
		pc.sendPackets(new S_SystemMessage(pc.getMap().toString(
				pc.getLocation())));
	}*/

	// ??????
	public C_MoveChar(byte decrypt[], LineageClient _client)
			throws Exception {
		super(decrypt);
		try {
			int locx = readH();
			int locy = readH();
			int heading = readC();

			L1PcInstance pc = _client.getActiveChar();
			if (pc == null) {
				return;
			}
			
			if (pc.isOnlyStopMove()) {
				return;
			}
			
			if (pc.isPrivateShop()|| pc.isDead() ) {
				return;
			}
			
			if (pc.isStop()) {
				return;
			}

			if (pc.isTeleport()) { // ????????????????????????
				return;
			}
			
			pc.setCheck(false);
			
			// ???????????????
			final int oleLocx = pc.getX();
			final int oleLocy = pc.getY();
						
			pc.setOleLocX(oleLocx);
			pc.setOleLocY(oleLocy);
						
			if (Config.CHECK_MOVE_INTERVAL) {
				final int result = pc.speed_Attack().checkIntervalmove();
				if (result == AcceleratorChecker.R_DISPOSED) {
					if (!pc.isGm()) {
						//WriteLogTxt.Recording("??????????????????","??????"+this._activeChar.getName()+"??????");
						pc.sendPackets(new S_SystemMessage("?????????????????????!"));
						pc.sendPackets(new S_Lock());
						return;
						//isError = true;
					}else {
						pc.sendPackets(new S_SystemMessage("?????????????????????!"));
					}				
				}
			}				
			//if (_client.getLanguage() == 3) {
				//heading ^= 0x49;
			//}
			if (heading>7) {
				return;
			}
			if (heading<0) {
				return;
			}
			
			boolean iserror = false;
			if (_client.getLanguage() != 3) {
				if ((locx != oleLocx) || (locy != oleLocy)) {
					iserror = true;
				}
			}	
			// ???????????????
			final int newlocx = oleLocx + HEADING_TABLE_X[heading];
			final int newlocy = oleLocy + HEADING_TABLE_Y[heading];
			for (L1Object obj : L1World.getInstance().getVisibleObjects(pc, 1)) {
				if (obj instanceof L1PcInstance) {
					L1PcInstance tgpc = (L1PcInstance)obj;
					if (tgpc.isGhost()) {
						continue;
					}
					if (tgpc.isGmInvis()) {
						continue;
					}
					if (pc.isGhost()) {
						continue;
					}
					if (pc.isGmInvis()) {
						continue;
					}
					if (tgpc.isDead()) {
						continue;
					}
					if (tgpc.getX()==newlocx&&tgpc.getY()==newlocy) {
						iserror = true;
						break;
					}
				}
				if (obj instanceof L1MonsterInstance) {
					L1MonsterInstance tgmob = (L1MonsterInstance)obj;
					if (tgmob.isDead()) {
						continue;
					}
					if (tgmob.getX()==newlocx&&tgmob.getY()==newlocy) {
						iserror = true;
						break;
					}
				}
			}
	/*		if (pc.getWarid()!=0) {
				if (!pc.getMap().isPassable2(newlocx, newlocy)) {
					iserror = true;
				}
			}*/
			
			if (iserror) {
				pc.sendPackets(new S_Lock());
				return;
			}
			pc.killSkillEffectTimer(L1SkillId.MEDITATION);

			if (!pc.hasSkillEffect(L1SkillId.ABSOLUTE_BARRIER)) { // ??????????????????????????????????????????
				pc.setRegenState(REGENSTATE_MOVE);
			}
			
			if (!pc.isGmInvis()) {
				pc.getMap().setPassable(pc.getLocation(), true);
			}

			int newMapId = Dungeon.getInstance().getNewMapId(newlocx, newlocy, pc.getMap().getId(), pc);//??????????????????mapid
			if(0 != newMapId){//??????????????????????????????????????????
				MapsNotAllowedTable mapInstance = MapsNotAllowedTable.getInstance();
				int allowLevel = mapInstance.getMapAllowLevel(newMapId);//???????????????????????????
				if(0 == mapInstance.getMapAllow(newMapId)){
					pc.sendPackets(new S_SystemMessage("???????????????????????????!"));
					return;
				}else if(pc.getLevel() < allowLevel){
					pc.sendPackets(new S_SystemMessage("???????????????"+allowLevel+"????????????????????????!"));
					return;
				}
			}

			if (Dungeon.getInstance().dg(newlocx, newlocy, pc.getMap().getId(), pc)) { // ?????????????????????????????????????????????
				return;
			}
			if (DungeonRandom.getInstance().dg(newlocx, newlocy, pc.getMap().getId(),
					pc)) { // ?????????????????????????????????????????????????????????
				return;
			}

			
			if (!pc.getPowerMap().isPassable2(newlocx, newlocy)) {
				pc.sendPackets(new S_SystemMessage("??????????????????????????????????????????????????????"));
				pc.sendPackets(new S_Lock());
				return;
			}
			if (pc.isCheckFZ()) {
				WriteLogTxt.Recording(pc.getName()+"??????","??????ID"+pc.getTempCharGfx()+" ??????"+pc.getWeapon().getLogViewName()+"??????");
			}

			pc.getLocation().set(newlocx, newlocy);
			pc.setHeading(heading);
			if (!pc.isGmInvis() && !pc.isGhost() && !pc.isInvisble()) {
				pc.broadcastPacket(new S_MoveCharPacket(pc));
			}

			// sendMapTileLog(pc); // ????????????????????????????????????(??????????????????)

			L1WorldTraps.getInstance().onPlayerMoved(pc);

			if (!pc.isGmInvis()&&!pc.isGhost()) {
				pc.getMap().setPassable(pc.getLocation(), false);
			}
			// user.UpdateObject(); // ?????????????????????????????????????????????
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}finally {
			this.over();
		}

	}
}