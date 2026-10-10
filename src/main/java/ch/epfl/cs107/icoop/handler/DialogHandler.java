package ch.epfl.cs107.icoop.handler;

import ch.epfl.cs107.play.engine.actor.Dialog;

/**
 * Interface DialogHandler.
 * Représente le système de gestion des dialogues dans le jeu.
 */
public interface DialogHandler {

    /**
     * Publie un dialogue.
     * @param dialog Le dialogue à publier.
     */
    void publish(Dialog dialog);
}
