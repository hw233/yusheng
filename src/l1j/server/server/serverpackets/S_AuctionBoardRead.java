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

import java.sql.*;
import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.Opcodes;

import l1j.server.server.utils.SQLUtil;

// Referenced classes of package l1j.server.server.serverpackets:
// ServerBasePacket

public class S_AuctionBoardRead extends ServerBasePacket {

	private static final Log _log = LogFactory.getLog(S_AuctionBoardRead.class);
	
	private static final String S_AUCTIONBOARDREAD = "[S] S_AuctionBoardRead";

	public S_AuctionBoardRead(int objectId, String house_number) {
		buildPacket(objectId, house_number);
	}

	private void buildPacket(int objectId, String house_number) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			int number = Integer.valueOf(house_number);
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM board_auction WHERE house_id=?");
			pstm.setInt(1, number);
			rs = pstm.executeQuery();
			while (rs.next()) {
				writeC(Opcodes.S_OPCODE_SHOWHTML);
				writeD(objectId);
				writeS("agsel");
				writeS(house_number); // 番号
				writeH(9); // 以下文字列个数
				writeS(rs.getString(2)); // 名前
				writeS(rs.getString(6)); // 位置
				writeS(String.valueOf(rs.getString(3))); // 广
				writeS(rs.getString(7)); // 以前所有者
				writeS(rs.getString(9)); // 现在入札者
				writeS(String.valueOf(rs.getInt(5))); // 现在入札价格
				Calendar cal = timestampToCalendar((Timestamp) rs.
						getObject(4));
				int month = cal.get(Calendar.MONTH) + 1;
				int day = cal.get(Calendar.DATE);
				int hour = cal.get(Calendar.HOUR_OF_DAY);
				writeS(String.valueOf(month)); // 缔切月
				writeS(String.valueOf(day)); // 缔切日
				writeS(String.valueOf(hour)); // 缔切时
			}
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	private Calendar timestampToCalendar(Timestamp ts) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(ts.getTime());
		return cal;
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}

	public String getType() {
		return S_AUCTIONBOARDREAD;
	}
}
