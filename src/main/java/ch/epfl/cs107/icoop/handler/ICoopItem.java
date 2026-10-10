package ch.epfl.cs107.icoop.handler;

import ch.epfl.cs107.icoop.actor.Orb;
import ch.epfl.cs107.play.areagame.handler.Inventory;
import ch.epfl.cs107.play.areagame.handler.InventoryItem;
import ch.epfl.cs107.play.engine.actor.Sprite;

/**
 * L'énumération ICoopItem représente une collection d'objets d'inventaire pour ICoopPlayer.
 * Chaque objet possède un nom, un identifiant de poche et un nom de sprite.
 */
public enum ICoopItem implements InventoryItem {

    /** Énumération des objets disponibles dans ICoop. */
    SWORD(0, "Sword", "icoop/sword.icon"),
    FIREKEY(0, "Fire Key", "icoop/key_red"),
    WATERKEY(0, "Water Key", "icoop/key_blue"),
    FIRESTAFF(0, "Fire Staff", "icoop/staff_fire.icon"),
    WATERSTAFF(0, "Water Staff", "icoop/staff_water.icon"),
    EXPLOSIVE(0, "Explosive", "icoop/explosive");

    /** Identifiant de poche associé à l'objet. */
    private final int pocketId;

    /** Nom de l'objet. */
    private final String name;

    /** Nom du sprite associé à l'objet. */
    private final String spriteName;

    /**
     * Constructeur de l'énumération ICoopItem.
     * @param pocketId Identifiant de poche associé à l'objet.
     * @param name Nom de l'objet.
     * @param spriteName Nom du sprite associé à l'objet.
     */
    ICoopItem(int pocketId, String name, String spriteName) {
        this.pocketId = pocketId;
        this.name = name;
        this.spriteName = spriteName;
    }

    /**
     * Renvoie l'identifiant de poche associé à l'objet.
     * @return L'identifiant de poche.
     */
    @Override
    public int getPocketId() {
        return pocketId;
    }

    /**
     * Renvoie le nom de l'objet.
     * @return Le nom de l'objet.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Renvoie le nom du sprite associé à l'objet.
     * @return Le nom du sprite.
     */
    public String getSpritename() {
        return spriteName;
    }
}
