package net.omar.tutorial.Vaults;

import net.omar.tutorial.Data.Indexes;

public class MyShulker extends InventoryEntry {
    public MyShulker(String name) {
        super(name);
    }

    @Override
    public void update(String operation) {
        InventorySaver.updateInventoryState(name, operation, Indexes.Shulker.SHULKER_BOX, this);
        InventorySaver.Inventory("Inventory").updateFromShulker();
    }


}
