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
package l1j.server.server.command.executor;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.server.model.L1Party;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.world.L1World;

public class L1PartyRecall implements L1CommandExecutor {	
	
	private static final Log _log = LogFactory.getLog(L1PartyRecall.class);

	private L1PartyRecall() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1PartyRecall();
	}

	// @Override
	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		L1PcInstance target = L1World.getInstance().getPlayer(arg);

		if (target != null) {
			L1Party party = target.getParty();
			if (party != null) {
				int x = pc.getX();
				int y = pc.getY() + 2;
				short map = pc.getMapId();
				L1PcInstance[] players = party.getMembers();
				for (L1PcInstance pc2 : players) {
					try {
						L1Teleport.teleport(pc2, x, y, map, 5, true);
						pc2.sendPackets(new S_SystemMessage("您被GM召唤到身边。"));
					} catch (Exception e) {
						_log.error(e.getLocalizedMessage(), e);
					}
				}
			} else {
				pc.sendPackets(new S_SystemMessage("请输入要召唤的角色名称。"));
			}
		} else {
			pc.sendPackets(new S_SystemMessage(arg + "不在线上。"));
		}
	}
}
