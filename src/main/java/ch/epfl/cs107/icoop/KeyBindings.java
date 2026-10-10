package ch.epfl.cs107.icoop;

import ch.epfl.cs107.play.window.Keyboard;

import static ch.epfl.cs107.play.window.Keyboard.A;
import static ch.epfl.cs107.play.window.Keyboard.D;
import static ch.epfl.cs107.play.window.Keyboard.E;
import static ch.epfl.cs107.play.window.Keyboard.I;
import static ch.epfl.cs107.play.window.Keyboard.J;
import static ch.epfl.cs107.play.window.Keyboard.K;
import static ch.epfl.cs107.play.window.Keyboard.L;
import static ch.epfl.cs107.play.window.Keyboard.O;
import static ch.epfl.cs107.play.window.Keyboard.Q;
import static ch.epfl.cs107.play.window.Keyboard.R;
import static ch.epfl.cs107.play.window.Keyboard.S;
import static ch.epfl.cs107.play.window.Keyboard.SPACE;
import static ch.epfl.cs107.play.window.Keyboard.T;
import static ch.epfl.cs107.play.window.Keyboard.U;
import static ch.epfl.cs107.play.window.Keyboard.W;

/**
 * Interface KeyboardConfig
 * Definition of movement keys for both players, as well as other global actions in the game.
 */
public final class KeyBindings {

    /**
     * Keys used for the red player.
     */
    public static final PlayerKeyBindings RED_PLAYER_KEY_BINDINGS = new PlayerKeyBindings(W, A, S, D, Q, E);

    /**
     * Keys used for the blue player.
     */
    public static final PlayerKeyBindings BLUE_PLAYER_KEY_BINDINGS = new PlayerKeyBindings(I, J, K, L, U, O);

    /**
     * Attribut SPACE.
     * Key to pass the next dialog.
     */
    public static final int NEXT_DIALOG = SPACE;

    /**
     * Attribut R.
     * Key to reset the game.
     */
    public static final int RESET_GAME = R;

    /**
     * Attribut T.
     * Key to reset the area
     */
    public static final int RESET_AREA = T;

    /**
     * Keys used by the player
     *
     * @param up         Pour le déplacement vers le haut
     * @param left       Pour le déplacement vers la gauche
     * @param down       Pour le déplacement vers le bas
     * @param right      Pour le déplacement vers la droite
     * @param switchItem Pour changer d'objet en main
     * @param useItem    Pour utiliser l'objet en main
     */
    public record PlayerKeyBindings(int up, int left, int down, int right, int switchItem, int useItem) {
    }
}
