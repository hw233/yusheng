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

package l1j.server.server.clientpackets;

import java.util.logging.Logger;



import l1j.server.server.WarTimeController;
import l1j.server.server.WriteLogTxt;
import l1j.server.server.datatables.CastleTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.mina.LineageClient;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Castle;
import l1j.server.server.world.L1World;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_Drawal extends ClientBasePacket {

	private static final String C_DRAWAL = "[C] C_Drawal";

	public C_Drawal(byte abyte0[], LineageClient _client)
			throws Exception {
		super(abyte0);
		int i = readD();
		int j = readD();

		L1PcInstance pc = _client.getActiveChar();
		L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
		if (clan != null) {
			int castle_id = clan.getCastleId();
			if (castle_id != 0) {
				L1Castle l1castle = CastleTable.getInstance().getCastleTable(
						castle_id);
				if (WarTimeController.getInstance().isNowWar(castle_id)) {
					pc.sendPackets(new S_SystemMessage("??????????????????????????????????????????"));
					return;
				}
				long money = l1castle.getPublicMoney();
				if (money<j) {
					return;
				}
				money -= j;
				L1ItemInstance item = ItemTable.getInstance().createItem(
						L1ItemId.ADENA);
				if (item != null) {
					l1castle.setPublicMoney(money);
					CastleTable.getInstance().updateCastle(l1castle);
					if (pc.getInventory().checkAddItem(item, j) == L1Inventory.OK) {
						pc.getInventory().storeItem(L1ItemId.ADENA, j);
					} else {
						L1World.getInstance().getInventory(pc.getX(),
								pc.getY(), pc.getMapId()).storeItem(
								L1ItemId.ADENA, j);
					}
					pc.sendPackets(new S_ServerMessage(143, "$457", "$4" + " ("
							+ j + ")"));
					WriteLogTxt.Recording(
							"??????????????????","??????#"+pc.getName()
							+"?????????ID"+pc.getMapId()+"X:"+pc.getX()+"Y:"+pc.getY()+"#??????objid???<"+pc.getId()+">"
					        +"#????????????"+item.getLogViewName()+"??????objid???<"+item.getId()+">"
							+"("+j+")??????"
							);
				}
			}
		}
	}

	@Override
	public String getType() {
		return C_DRAWAL;
	}

}
