package net.omar.tutorial.Vaults;

import net.omar.tutorial.Data.Indexes;

public class MyInventory extends InventoryEntry {
    public static final String NAME = "Inventory";
    public MyInventory(String name) {
        super(name);
    }

    @Override
    public void update(String operation) {
        InventorySaver.updateInventoryState(name, operation, Indexes.Inventory.TOTAL_INVENTORY, this);
    }

    public void updateFromPV() {
        InventorySaver.updateInventoryState(name, "Update from PV", Indexes.PV.TOTAL_INVENTORY, this);
    }

    public void updateFromShulker() {
        InventorySaver.updateInventoryState(name, "Update from Shulker", Indexes.Shulker.TOTAL_INVENTORY, this);
    }

    public void updateFromTrade() {
        InventorySaver.updateInventoryState(name, "Update from Trade", Indexes.Trade.TOTAL_INVENTORY, this);
    }
}
