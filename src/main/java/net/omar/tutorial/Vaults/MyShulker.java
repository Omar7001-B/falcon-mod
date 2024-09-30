package net.omar.tutorial.Vaults;

import net.omar.tutorial.Data.Indexes;

public class MyShulker extends InventoryEntry {
    public MyShulker(String name) {
        super(name);
    }

    @Override
    public void update(String operation) {
        VaultsStateManager.updateInventoryState(name, operation, Indexes.Shulker.SHULKER_BOX, this);
        VaultsStateManager.Inventory("Inventory").updateFromShulker();
    }


}
