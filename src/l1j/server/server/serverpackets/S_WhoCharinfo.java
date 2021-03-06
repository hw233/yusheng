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
package l1j.server.server.serverpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1PcInstance;

public class S_WhoCharinfo extends ServerBasePacket {
	private static final String S_WHO_CHARINFO = "[S] S_WhoCharinfo";
	
	private static final Log _log = LogFactory.getLog(S_WhoCharinfo.class);


	public S_WhoCharinfo(L1PcInstance pc) {
		_log.info("Who charpack for : " + pc.getName());

		String lawfulness = "";
		int lawful = pc.getLawful();
		if (lawful < 0) {
			lawfulness = "(Chaotic)";
		} else if (lawful >= 0 && lawful < 500) {
			lawfulness = "(Neutral)";
		} else if (lawful >= 500) {
			lawfulness = "(Lawful)";
		}

		writeC(Opcodes.S_OPCODE_MSG);
		writeC(0x08);

		String title = "";
		String clan = "";

		if (pc.getTitle().equalsIgnoreCase("") == false) {
			title = pc.getTitle() + " ";
		}

		if (pc.getClanid() > 0) {
			clan = "[" + pc.getClanname() + "]";
		}

		writeS(title + pc.getName() + " " + lawfulness + " " + clan);
		// writeD(0x80157FE4);
		writeD(0);
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}

	public String getType() {
		return S_WHO_CHARINFO;
	}
}
