package ch.epfl.cs107.icoop;


import ch.epfl.cs107.icoop.actor.*;
import ch.epfl.cs107.icoop.area.*;
import ch.epfl.cs107.icoop.handler.ICoopItem;
import ch.epfl.cs107.play.areagame.AreaGame;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Window;

import java.util.List;

/**
 * Classe ICoop.
 * Représente la classe principale du jeu ICoop, gère l'initialisation, les mises à jour et les transitions.
 */
public class ICoop extends AreaGame {

    /** Noms des différentes aires du jeu (utilisés pour les titres). */
    private final String[] areas = {"Spawn", "OrbWay", "Maze", "Arena"};

    /** Joueurs ICoopPlayer. */
    private ICoopPlayer player1, player2;

    /** Indice de l'aire courante. */
    private int areaIndex;

    /** Aire courante. */
    private ICoopArea area;

    /**
     * Crée une instance de CenterOfMass reliant les deux joueurs.
     * @return Une instance de CenterOfMass.
     */
    private CenterOfMass createCenterOfMass() {
        return new CenterOfMass(player1, player2);
    }

    /**
     * Crée toutes les aires du jeu.
     */
    private void createAreas() {
        Arena arena = new Arena(getWindow());
        addArea(new Spawn());
        addArea(new OrbWay());
        addArea(new Maze());
        addArea(arena);
    }

    /**
     * Initialise le jeu.
     * @param window (Window) : contexte d'affichage, non null.
     * @param fileSystem (FileSystem) : système de fichiers donné, non null.
     * @return True si le jeu commence correctement, sinon False.
     */
    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            createAreas();
            areaIndex = 0; // Indice initial correspondant à Spawn
            initArea(areas[areaIndex]);
            if (area != null) postInitArea();
            return true;
        }
        return false;
    }

    /**
     * Méthode de mise à jour appelée à chaque frame.
     * @param deltaTime Temps écoulé depuis la dernière mise à jour, en secondes, non négatif.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (area.getKeyboard().get(KeyBindings.RESET_GAME).isPressed()) resetGame();
        if (area.getKeyboard().get(KeyBindings.RESET_AREA).isPressed()) resetArea();

        if (player1.getDialog() == null && player2.getDialog() == null) {
            // Mise à jour du jeu
            if (area.getTitle().equals("Spawn") && player1.getInventory().getInventoryItems().contains(ICoopItem.FIRESTAFF) && player2.getInventory().getInventoryItems().contains(ICoopItem.WATERSTAFF)) {
                player1.setReadyToWin();
            }

            CenterOfMass centerOfMass = createCenterOfMass();
            getCurrentArea().setViewCandidate(centerOfMass);

            // Vérification de l'état des joueurs
            if (player1.isDead() || player2.isDead()) resetArea();

            // Gestion de la caméra
            float distance = player1.getPosition().sub(player2.getPosition()).getLength();
            float scaleFactor = Math.max(ICoopArea.DEFAULT_SCALE_FACTOR, ICoopArea.DEFAULT_SCALE_FACTOR * 0.75f + distance / 2);

            // Gestion des transitions via les portes et portails
            handleDoorTransitionExtendsUpdate();
        }
    }

    /**
     * Termine le jeu.
     * Non implémenté.
     */
    @Override
    public void end() {
        // Non utilisé mais obligatoire à surcharger.
    }

    /**
     * Renvoie le titre du jeu.
     * @return "ICoopGame" comme titre du jeu.
     */
    @Override
    public String getTitle() {
        return "ICoopGame";
    }

    /**
     * Initialise une aire donnée et configure les joueurs dans cette aire.
     * @param areaKey Clé de l'aire à initialiser.
     */
    private void initArea(String areaKey) {
        area = (ICoopArea) setCurrentArea(areaKey, true);
        player1 = new ICoopPlayer(area, area.getPlayerOrientation(), new DiscreteCoordinates(0, 0), "icoop/player", "FEU", KeyBindings.RED_PLAYER_KEY_BINDINGS);
        player2 = new ICoopPlayer(area, area.getPlayerOrientation(), new DiscreteCoordinates(0, 0), "icoop/player2", "EAU", KeyBindings.BLUE_PLAYER_KEY_BINDINGS);
        player1.enterArea(area, area.getPlayerPosition(player1));
        player2.enterArea(area, area.getPlayerPosition(player2));
        player1.centerCamera();
    }

    /**
     * Configure les dialogues d'accueil après l'initialisation de l'aire.
     */
    private void postInitArea() {
        player1.resetDialogTimer(); player2.resetDialogTimer();
        player1.setActiveDialog("welcome"); player2.setActiveDialog("welcome");
    }

    /**
     * Change les joueurs d'aire.
     * @param pathArea Nom de la nouvelle aire.
     * @param coords Coordonnées des joueurs dans la nouvelle aire.
     */
    private void switchArea(String pathArea, List<DiscreteCoordinates> coords) {
        player1.leaveArea();
        player2.leaveArea();
        area = (ICoopArea) setCurrentArea(pathArea, false);
        if (coords != null) {
            player1.enterArea(area, coords.get(0));
            player2.enterArea(area, coords.get(1));
        } else {
            player1.enterArea(area, area.getPlayerPosition(player1));
            player2.enterArea(area, area.getPlayerPosition(player2));
        }
    }

    /**
     * Gère les transitions entre les portes et portails.
     * Séparé de l'update pour plus de clarté.
     */
    public void handleDoorTransitionExtendsUpdate() {
        if (player1.getCurrentDoor() != null || player2.getCurrentDoor() != null) {
            if (player1.getPathArea() != null) {
                switchArea(player1.getPathArea(), player1.getPositionPathArea());
                player1.setOrientation(player1.getOrientation());
                player2.setOrientation(player1.getOrientation());
                player1.traverseDoor(null, null, null);
            } else {
                switchArea(player2.getPathArea(), player2.getPositionPathArea());
                player1.setOrientation(player2.getOrientation());
                player2.setOrientation(player2.getOrientation());
                player2.traverseDoor(null, null, null);
            }
        } else if (player1.getCurrentPortal() != null || player2.getCurrentPortal() != null) {
            if (player1.getPathArea() != null) {
                switchArea(player1.getPathArea(), player1.getPositionPathArea());
                player1.setOrientation(player1.getOrientation());
                player2.setOrientation(player1.getOrientation());
                player1.traversePortal(null, null);
            } else {
                switchArea(player2.getPathArea(), player2.getPositionPathArea());
                player1.setOrientation(player2.getOrientation());
                player2.setOrientation(player2.getOrientation());
                player2.traversePortal(null, null);
            }
        }
    }

    /**
     * Réinitialise l'aire courante.
     */
    public void resetArea() {
        if (area != null) initArea(area.getTitle());
    }

    /**
     * Réinitialise le jeu entier.
     */
    public void resetGame() {
        if (this.getWindow() != null && this.getFileSystem() != null) {
            begin(this.getWindow(), this.getFileSystem());
        }
    }
}