package ch.epfl.cs107.icoop.actor;

/**
 * Interface pour les entités possédant un type d'élément.
 */
public interface ElementalEntity {

    /**
     * Renvoie le type d'élément de l'entité.
     *
     * @return Une chaîne de caractères représentant l'élément de l'entité.
     */
    String element();
}
