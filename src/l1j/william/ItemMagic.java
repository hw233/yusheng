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

package l1j.william;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.SQLUtil;
import l1j.william.L1WilliamItemMagic;

public class ItemMagic {
	private static final Log _log = LogFactory.getLog(ItemMagic.class);


	private static ItemMagic _instance;

	private final HashMap<Integer, L1WilliamItemMagic> _itemIdIndex
			= new HashMap<Integer, L1WilliamItemMagic>();

	public static ItemMagic getInstance() {
		if (_instance == null) {
			_instance = new ItemMagic();
		}
		return _instance;
	}

	private ItemMagic() {
		loadItemMagic();
	}

	private void loadItemMagic() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {

			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM william_item_magic");
			rs = pstm.executeQuery();
			fillItemMagic(rs);
		} catch (SQLException e) {
			_log.info("error while creating william_item_magic table",
					e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	private void fillItemMagic(ResultSet rs) throws SQLException {
		while (rs.next()) {
			int item_id = rs.getInt("item_id");
			int checkClass = rs.getInt("checkClass");
			int checkItem = rs.getInt("checkItem");
			int skill_id = rs.getInt("skill_id");
			int removeItem = rs.getInt("removeItem");
			
			
			L1WilliamItemMagic Item_Magic = new L1WilliamItemMagic(item_id, checkClass, checkItem, skill_id, removeItem);
			_itemIdIndex.put(item_id, Item_Magic);
		}
	}

	public L1WilliamItemMagic getTemplate(int itemId) {
		return _itemIdIndex.get(itemId);
	}
}
