package org.bukkit.entity;

import java.util.List;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.MerchantRecipe;

/**
 * Represents a villager NPC
 */
public interface Villager extends Ageable, NPC, InventoryHolder {

    /**
     * Gets the current profession of this villager.
     *
     * @return Current profession.
     */
    public Profession getProfession();

    /**
     * Sets the new profession of this villager.
     *
     * @param profession New profession.
     */
    public void setProfession(Profession profession);

    /**
     * Get a list of trades currently available from this villager.
     *
     * @return an immutable list of trades
     */
    List<MerchantRecipe> getRecipes();

    /**
     * Set the list of trades currently available from this villager.
     * <br>
     * This will not change the selected trades of players currently trading
     * with this villager.
     *
     * @param recipes a list of recipes
     */
    void setRecipes(List<MerchantRecipe> recipes);

    /**
     * Get the recipe at a certain index of this villager's trade list.
     *
     * @param i the index
     * @return the recipe
     * @throws IndexOutOfBoundsException
     */
    MerchantRecipe getRecipe(int i) throws IndexOutOfBoundsException;

    /**
     * Set the recipe at a certain index of this villager's trade list.
     *
     * @param i the index
     * @param recipe the recipe
     * @throws IndexOutOfBoundsException
     */
    void setRecipe(int i, MerchantRecipe recipe) throws IndexOutOfBoundsException;

    /**
     * Get the number of trades this villager currently has available.
     *
     * @return the recipe count
     */
    int getRecipeCount();

    /**
     * Gets this villager's inventory.
     * <br>
     * Note that this inventory is not the Merchant inventory, rather, it is the
     * items that a villager might have collected (from harvesting crops, etc.)
     *
     * @inheritDoc
     */
    @Override
    Inventory getInventory();

    /**
     * Gets whether this villager is currently trading.
     *
     * @return whether the villager is trading
     */
    boolean isTrading();

    /**
     * Gets the player this villager is trading with, or null if it is not
     * currently trading.
     *
     * @return the trader, or null
     */
    HumanEntity getTrader();

    /**
     * Gets this villager's riches, the number of emeralds this villager has
     * been given.
     *
     * @return the villager's riches
     */
    int getRiches();

    /**
     * Sets this villager's riches.
     *
     * @see Villager#getRiches()
     *
     * @param riches the new riches
     */
    void setRiches(int riches);

    /**
     * Represents the various different Villager professions there may be.
     */
    public enum Profession {
        FARMER(0),
        LIBRARIAN(1),
        PRIEST(2),
        BLACKSMITH(3),
        BUTCHER(4);

        private static final Profession[] professions = new Profession[Profession.values().length];
        private final int id;

        static {
            for (Profession type : values()) {
                professions[type.getId()] = type;
            }
        }

        private Profession(int id) {
            this.id = id;
        }

        /**
         * Gets the ID of this profession.
         *
         * @return Profession ID.
         * @deprecated Magic value
         */
        @Deprecated
        public int getId() {
            return id;
        }

        /**
         * Gets a profession by its ID.
         *
         * @param id ID of the profession to get.
         * @return Resulting profession, or null if not found.
         * @deprecated Magic value
         */
        @Deprecated
        public static Profession getProfession(int id) {
            return (id >= professions.length) ? null : professions[id];
        }
    }
}
