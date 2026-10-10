package ch.epfl.cs107.icoop.handler;

import ch.epfl.cs107.play.areagame.handler.Inventory;
import ch.epfl.cs107.play.areagame.handler.InventoryItem;

import java.util.*;

/**
 * Classe ICoopInventory.
 * Représente l'inventaire des joueurs ICoopPlayers.
 */
public class ICoopInventory extends Inventory {

    /** Carte triée contenant les éléments de l'inventaire et leurs quantités. */
    private final NavigableMap<ICoopItem, Integer> items;

    /** Élément actuellement sélectionné par le joueur. */
    private ICoopItem currentItem;

    /**
     * Constructeur de la classe ICoopInventory.
     * Initialise un inventaire vide.
     */
    public ICoopInventory() {
        this.items = new TreeMap<>();
    }

    /**
     * Ajoute un élément à l'inventaire.
     * @param item L'élément à ajouter.
     * @return True si l'élément a été ajouté, sinon False.
     */
    public boolean addItem(ICoopItem item) {
        if (item == null) return false;
        if (getCurrentItem() == null) setCurrentItem(item);
        items.put(item, items.getOrDefault(item, 0) + 1);
        return true;
    }

    /**
     * Renvoie l'élément actuellement sélectionné par le joueur.
     * @return L'élément actuel.
     */
    public ICoopItem getCurrentItem() {
        return currentItem;
    }

    /**
     * Sélectionne un élément donné comme élément actuel.
     * @param item L'élément à sélectionner.
     */
    public void setCurrentItem(ICoopItem item) {
        this.currentItem = item;
    }

    /**
     * Retire un élément de l'inventaire.
     * @param item L'élément à retirer.
     * @return True si l'élément a été retiré, sinon False.
     */
    public boolean removeItem(ICoopItem item) {
        if (item == null || !items.containsKey(item)) return false;

        int currentQuantity = items.get(item);
        if (currentQuantity > 1) {
            items.put(item, currentQuantity - 1);
        } else {
            items.remove(item);
        }
        return true;
    }

    /**
     * Renvoie la liste de tous les éléments présents dans l'inventaire.
     * @return Liste des éléments de l'inventaire.
     */
    public List<ICoopItem> getInventoryItems() {
        return new ArrayList<>(items.keySet());
    }

    /**
     * Renvoie le prochain élément dans la liste des éléments, avec boucle au début si l'élément courant est le dernier.
     * @param list Liste des éléments.
     * @param index Index actuel.
     * @return Le prochain élément.
     */
    public ICoopItem getNextItemPosition(List<ICoopItem> list, int index) {
        if (list.isEmpty()) {
            return null;
        }
        int nextIndex = (index + 1) % list.size();
        return list.get(nextIndex);
    }
}