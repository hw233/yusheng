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
package l1j.server.server.model.shop;

import java.util.ArrayList;
import java.util.List;

import l1j.server.Config;
import l1j.server.server.model.L1TaxCalculator;
import l1j.server.server.templates.L1ShopItem;

class L1ShopBuyOrder {
	private final L1ShopItem _item;
	private final int _count;

	public L1ShopBuyOrder(L1ShopItem item, int count) {
		_item = item;
		_count = Math.max(1, count);
	}

	public L1ShopItem getItem() {
		return _item;
	}

	public int getCount() {
		return _count;
	}
}

public class L1ShopBuyOrderList {
	private final L1Shop _shop;
	private final List<L1ShopBuyOrder> _list = new ArrayList<L1ShopBuyOrder>();
	private final L1TaxCalculator _taxCalc;

	private int _totalWeight = 0;
	private long _totalPrice = 0;
	private long _totalPriceTaxIncluded = 0;

	L1ShopBuyOrderList(L1Shop shop) {
		_shop = shop;
		_taxCalc = new L1TaxCalculator(shop.getNpcId());
	}

	public void add(int orderNumber, int count) {
		if (_shop.getSellingItems().size() < orderNumber) {
			return;
		}
		if (count <= 0) {
			return;
		}
		if (count > 1000) {
			return;
		}
		L1ShopItem shopItem = _shop.getSellingItems().get(orderNumber);

		long price = (long) (shopItem.getPrice() * Config.RATE_SHOP_SELLING_PRICE);
		// 
		for (int j = 0; j < count; j++) {
			if (price * j < 0) {
				return;
			}
		}
		_totalPrice += price * count;
		_totalPriceTaxIncluded += _taxCalc.layTax(price) * count;
		_totalWeight += shopItem.getItem().getWeight() * count
				* shopItem.getPackCount();

		if (shopItem.getItem().isStackable()) {
			_list.add(new L1ShopBuyOrder(shopItem, count
					* shopItem.getPackCount()));
			return;
		}

		for (int i = 0; i < (count * shopItem.getPackCount()); i++) {
			_list.add(new L1ShopBuyOrder(shopItem, 1));
		}
	}

	List<L1ShopBuyOrder> getList() {
		return _list;
	}

	public int getTotalWeight() {
		return _totalWeight;
	}

	public long getTotalPrice() {
		return _totalPrice;
	}

	public long getTotalPriceTaxIncluded() {
		return _totalPriceTaxIncluded;
	}

	L1TaxCalculator getTaxCalculator() {
		return _taxCalc;
	}
}
