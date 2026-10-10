package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.KeyBindings;
import ch.epfl.cs107.icoop.area.ICoopArea;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.icoop.handler.ICoopInventory;
import ch.epfl.cs107.icoop.handler.ICoopItem;
import ch.epfl.cs107.icoop.handler.ICoopPlayerStatusGUI;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Dialog;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Transform;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Button;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

import java.util.*;
import java.util.List;

import static ch.epfl.cs107.play.math.Orientation.*;

/**
 * Classe représentant un joueur dans le jeu ICoop.
 * Cette classe gère les fonctionnalités principales du joueur, notamment les interactions avec
 * les objets, les ennemis, et les environnements du jeu.
 * Elle hérite de MovableAreaEntity et implémente ElementalEntity et Interactor.
 */
public final class ICoopPlayer extends MovableAreaEntity implements ElementalEntity, Interactor {

    /**
     * Durée d'un déplacement du joueur.
     */
    private final int MOVE_DURATION = 4;

    /**
     * Durée de l'animation d'une attaque avec l'épée.
     */
    private final int SWORD_ANIMATION_DURATION = 2;

    /**
     * Durée de l'animation d'une attaque avec un bâton.
     */
    private final int STAFF_ANIMATION_DURATION = 2;

    /**
     * Ordres de direction dans lesquels le joueur peut se déplacer.
     */
    private final Orientation[] orders = {Orientation.DOWN, RIGHT, UP, LEFT};

    /**
     * Animation orientée associée au joueur.
     */
    private OrientedAnimation animation;

    /**
     * Nom du sprite associé au joueur.
     */
    private final String spriteName;

    /**
     * Instance du clavier pour contrôler le joueur.
     */
    private Keyboard keyboard = getOwnerArea().getKeyboard();

    /**
     * Type d'élément associé au joueur (eau, feu, etc.).
     */
    private final String elementType;

    /**
     * Clavier lié à la configuration des touches du joueur.
     */
    private final KeyBindings.PlayerKeyBindings keyboardKey;

    /**
     * Santé du joueur.
     */
    private final Health health;

    /**
     * Santé maximale du joueur.
     */
    private final int MAX_LIFE = 20;

    /**
     * Temporisateurs pour gérer l'invulnérabilité par type de dégâts.
     */
    private final Map<DamageType, Float> invulnerabilityTimers;

    /**
     * Temporisateur global d'invulnérabilité.
     */
    private float globalInvulnerabilityTimer = 3.0f;

    /**
     * Temporisateur pour les dialogues.
     */
    private float dialogTimer = 0.0f;

    /**
     * Mise à jour des animations.
     */
    private float animationupdate;

    /**
     * Instance pour les dialogues actifs.
     */
    private Dialog dialog;

    /**
     * État courant du joueur.
     */
    private State state;

    /**
     * Référence à la porte du manoir.
     */
    private ManorDoor manorDoor;

    /**
     * Inventaire du joueur.
     */
    private final ICoopInventory inventory;

    /**
     * Indicateur si le sprite est inversé (par exemple, pour l'eau).
     */
    private final boolean flipped;

    /**
     * Interface GUI pour l'état du joueur.
     */
    private final ICoopPlayerStatusGUI statusGUI;

    /**
     * Gestionnaire des interactions du joueur.
     */
    private final ICoopPlayerInteractionHandler handler = new ICoopPlayerInteractionHandler();

    /**
     * Constructeur du joueur.
     *
     * @param owner       Zone du jeu propriétaire.
     * @param orientation Orientation initiale.
     * @param coordinates Coordonnées initiales.
     * @param spriteName  Nom du sprite associé.
     * @param element     Type d'élément (eau, feu, etc.).
     * @param keyboardKey Configuration des touches du joueur.
     */
    public ICoopPlayer(ICoopArea owner, Orientation orientation, DiscreteCoordinates coordinates, String spriteName, String element, KeyBindings.PlayerKeyBindings keyboardKey) {
        super(owner, orientation, coordinates);
        this.elementType = element;
        this.state = State.IDLE;
        this.spriteName = spriteName;
        defaultAnimation();
        this.keyboardKey = keyboardKey;
        this.health = new Health(this, Transform.I.translated(0, 1.75f), MAX_LIFE, true);
        this.invulnerabilityTimers = new HashMap<>();
        for (DamageType type : DamageType.values()) {
            invulnerabilityTimers.put(type, 0.0f);
        }
        this.inventory = new ICoopInventory();
        inventory.addItem(ICoopItem.SWORD);
        inventory.addItem(ICoopItem.FIRESTAFF);
        flipped = element.equals("EAU");
        statusGUI = new ICoopPlayerStatusGUI(this, flipped);
    }

    /**
     * Définit l'animation par défaut pour le joueur.
     */
    private void defaultAnimation() {
        final Vector anchor = new Vector(0, 0);
        this.animation = new OrientedAnimation(spriteName, MOVE_DURATION - 5, this, anchor, orders, 4, 1, 2, 16, 32, true);
    }

    /**
     * Renvoie le dialogue actif.
     *
     * @return Le dialogue actif ou null.
     */
    public Dialog getDialog() {
        return dialog;
    }

    /**
     * Enumération représentant les différents états possibles du joueur.
     */
    private enum State {
        IDLE,            // État inactif
        SWORD_ATTACK,    // État d'attaque à l'épée
        STAFF_ATTACK;    // État d'attaque avec un bâton
    }

    /**
     * Passe à l'élément suivant dans l'inventaire.
     */
    public void switchToNextItem() {
        List<ICoopItem> items = inventory.getInventoryItems();
        if (items.isEmpty()) {
            inventory.setCurrentItem(null);
            return;
        }
        int currentIndex = items.indexOf(inventory.getCurrentItem());
        inventory.setCurrentItem(inventory.getNextItemPosition(items, currentIndex));
    }

    /**
     * Utilise l'élément sélectionné si applicable.
     */
    public void useItemPress() {
        if (state == State.IDLE) {
            final Orientation[] orders = {DOWN, UP, RIGHT, LEFT};
            if (inventory.getCurrentItem() == ICoopItem.SWORD) useSwordItem(orders);
            if (inventory.getCurrentItem() == ICoopItem.FIRESTAFF) useStaffItem("FEU", orders);
            if (inventory.getCurrentItem() == ICoopItem.WATERSTAFF) useStaffItem("EAU", orders);
        }
        if (inventory.getCurrentItem() == ICoopItem.EXPLOSIVE) useExplosifItem();
    }

    /**
     * Utilise un objet explosif.
     */
    private void useExplosifItem() {
        Vector orientationVector = getOrientation().toVector();
        DiscreteCoordinates fieldOfViewCell = getCurrentMainCellCoordinates().jump(orientationVector);
        if (getOwnerArea().canEnterAreaCells(this, getFieldOfViewCells())) {
            inventory.removeItem(inventory.getCurrentItem());
            switchToNextItem();
            Explosif explosif = new Explosif(getOwnerArea(), Orientation.DOWN, fieldOfViewCell, 5, false);
            getOwnerArea().registerActor(explosif);
        }
    }

    /**
     * Utilise un objet épée pour attaquer.
     *
     * @param orders Ordres de direction pour l'animation.
     */
    private void useSwordItem(Orientation[] orders) {
        this.state = State.SWORD_ATTACK;
        final Vector anchor = new Vector(-.5f, 0);
        animation = new OrientedAnimation(spriteName + ".sword", SWORD_ANIMATION_DURATION, this, anchor, orders, 4, 2, 2, 32, 32);
    }

    /**
     * Utilise un bâton élémentaire pour attaquer.
     *
     * @param element Type d'élément ("FEU" ou "EAU").
     * @param orders  Ordres de direction pour l'animation.
     */
    private void useStaffItem(String element, Orientation[] orders) {
        this.state = State.STAFF_ATTACK;
        if (element.equals("FEU")) element = ".staff_fire";
        else if (element.equals("EAU")) element = ".staff_water";
        final Vector anchor = new Vector(-.5f, -.20f);
        animation = new OrientedAnimation(spriteName + element, STAFF_ANIMATION_DURATION, this, anchor, orders, 4, 2, 2, 32, 32);
    }

    /**
     * Génère un projectile associé à un élément.
     *
     * @param element Type d'élément ("FEU" ou "EAU").
     */
    private void getBall(String element) {
        Vector orientationVector = getOrientation().toVector();
        DiscreteCoordinates fieldOfViewCell = getCurrentMainCellCoordinates().jump(orientationVector);
        if (getOwnerArea().canEnterAreaCells(this, getFieldOfViewCells())) {
            new Ball(getOwnerArea(), getOrientation(), fieldOfViewCell, element, 4, 25, true);
        }
    }

    /**
     * Obtient l'inventaire du joueur.
     *
     * @return L'inventaire du joueur.
     */
    public ICoopInventory getInventory() {
        return inventory;
    }

    /**
     * Définit une clé pour le joueur.
     *
     * @param key La clé à associer au joueur.
     */
    private void setKey(Key key) {
        this.key = key;
    }

    /**
     * Met à jour l'état du joueur chaque frame.
     *
     * @param deltaTime Temps écoulé depuis la dernière mise à jour.
     */
    @Override
    public void update(float deltaTime) {
        if (dialog != null) {
            if (getOwnerArea().getKeyboard().get(KeyBindings.NEXT_DIALOG).isPressed()) {
                dialog.update(deltaTime);
                if (dialog.isCompleted()) {
                    dialogTimer = 1.0f;
                    dialog = null;
                }
            }
        } else {
            if (dialogTimer >= 0) dialogTimer -= deltaTime;
            if (state != State.IDLE) {
                animation.update(deltaTime);
                if (animation.isCompleted()) {
                    if (inventory.getCurrentItem() == ICoopItem.FIRESTAFF) getBall("FEU");
                    if (inventory.getCurrentItem() == ICoopItem.WATERSTAFF) getBall("EAU");
                    defaultAnimation();
                    this.state = State.IDLE;
                }
            }
            if (keyboard.get(keyboardKey.useItem()).isPressed()) {
                useItemPress();
            }
            if (keyboard.get(keyboardKey.switchItem()).isPressed()) {
                switchToNextItem();
            }

            if (moveIfPressed(UP, keyboard.get(keyboardKey.up()))) {
                animation.update(deltaTime);
            }
            if (moveIfPressed(LEFT, keyboard.get(keyboardKey.left()))) {
                animation.update(deltaTime);
            }
            if (moveIfPressed(Orientation.DOWN, keyboard.get(keyboardKey.down()))) {
                animation.update(deltaTime);
            }
            if (moveIfPressed(RIGHT, keyboard.get(keyboardKey.right()))) {
                animation.update(deltaTime);
            }
            if (globalInvulnerabilityTimer > 0) {
                globalInvulnerabilityTimer -= deltaTime;
                if (globalInvulnerabilityTimer <= 0) {
                    globalInvulnerabilityTimer = 0;
                }
            }
            for (Map.Entry<DamageType, Float> entry : invulnerabilityTimers.entrySet()) {
                float newTimer = entry.getValue() - deltaTime;
                if (newTimer <= 0) {
                    newTimer = 0.0f;
                } else animationupdate = newTimer;

                invulnerabilityTimers.put(entry.getKey(), newTimer);
            }
        }
        super.update(deltaTime);
    }

    /**
     * Dessine les éléments visuels associés au joueur.
     *
     * @param canvas Le canvas sur lequel dessiner.
     */
    @Override
    public void draw(Canvas canvas) {
        if (!(animationupdate % 0.2 <= 0.099) || (int) animationupdate == 0) animation.draw(canvas);
        health.draw(canvas);
        if (dialog != null) dialog.draw(canvas);
        statusGUI.draw(canvas);
    }

    /**
     * Indique si le joueur occupe une cellule de l'aire.
     * @return True, le joueur occupe une cellule.
     */
    @Override
    public boolean takeCellSpace() {
        return true;
    }

    /**
     * Vérifie si le joueur peut interagir au niveau des cellules.
     * @return True, le joueur est cellulairement interactif.
     */
    @Override
    public boolean isCellInteractable() {
        return true;
    }

    /**
     * Vérifie si le joueur peut interagir au niveau de la vue.
     * @return True, le joueur est visuellement interactif.
     */
    @Override
    public boolean isViewInteractable() {
        return true;
    }

    /**
     * Renvoie la liste des coordonnées des cellules actuelles occupées par le joueur.
     * @return Liste contenant les coordonnées de la cellule actuelle.
     */
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    /**
     * Renvoie les coordonnées des cellules dans le champ de vision du joueur.
     * @return Liste contenant une cellule dans le champ de vision.
     */
    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        DiscreteCoordinates mainCell = getCurrentMainCellCoordinates();
        Vector orientationVector = getOrientation().toVector();
        DiscreteCoordinates fieldOfViewCell = mainCell.jump(orientationVector);
        return Collections.singletonList(fieldOfViewCell);
    }

    /**
     * Vérifie si le joueur souhaite interagir au niveau des cellules.
     * @return True, le joueur souhaite une interaction cellulaire.
     */
    @Override
    public boolean wantsCellInteraction() {
        return true;
    }

    /**
     * Vérifie si le joueur souhaite interagir au niveau de la vue.
     * @return True, si le joueur appuie sur le bouton d'utilisation.
     */
    @Override
    public boolean wantsViewInteraction() {
        return keyboard.get(keyboardKey.useItem()).isPressed();
    }

    /**
     * Gère l'interaction avec un autre objet interactif.
     * @param other L'objet interactif.
     * @param isCellInteraction True si c'est une interaction cellulaire.
     */
    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler, isCellInteraction);
    }

    /**
     * Accepte une interaction d'un autre acteur.
     * @param v Visiteur d'interaction de zone.
     * @param isCellInteraction True si c'est une interaction cellulaire.
     */
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    /**
     * Retourne le type d'élément associé au joueur.
     * @return Le type d'élément ("FEU", "EAU", etc.).
     */
    @Override
    public String element() {
        return elementType;
    }

    /**
     * Réinitialise le temporisateur de dialogue.
     */
    public void resetDialogTimer() {
        dialogTimer = -0.1f;
    }

    /**
     * Enumération des types de dégâts possibles.
     */
    public enum DamageType {
        FIRE,       // Dégâts de type feu
        WATER,      // Dégâts de type eau
        EXPLOSION,  // Dégâts causés par des explosions
        PHYSICAL    // Dégâts physiques
    }

    /**
     * Vérifie si le joueur est invulnérable à un type de dégâts.
     *
     * @param damageType Le type de dégâts.
     * @return True si le joueur est invulnérable.
     */
    public boolean isInvulnerable(DamageType damageType) {
        if (globalInvulnerabilityTimer > 0) {
            return true;
        }
        float specificTimer = invulnerabilityTimers.getOrDefault(damageType, 0.0f);
        return specificTimer > 0;
    }

    /**
     * Vérifie si le joueur est mort.
     *
     * @return True si la santé est épuisée.
     */
    public boolean isDead() {
        return health.isOff();
    }

    /**
     * Applique des dégâts au joueur.
     *
     * @param damageType Le type de dégâts infligés.
     * @param amount     La quantité de dégâts.
     */
    public void applyDamage(DamageType damageType, int amount) {
        if (!isInvulnerable(damageType)) {
            health.decrease(amount);
            invulnerabilityTimers.put(damageType, 3.0f);
        }
    }

    /**
     * Soigne le joueur d'une quantité spécifiée.
     *
     * @param amount La quantité de points de vie restaurée.
     */
    public void heal(int amount) {
        health.increase(amount);
    }

    /**
     * Réinitialise les points de vie du joueur.
     */
    public void resetHealth() {
        health.resetHealth();
    }

    /**
     * Change l'orientation du joueur.
     *
     * @param orientation Nouvelle orientation.
     */
    public void setOrientation(Orientation orientation) {
        this.orientate(orientation);
    }

    /**
     * Définit un dialogue actif pour le joueur.
     *
     * @param dialog Texte du dialogue.
     */
    public void setActiveDialog(String dialog) {
        if (this.dialog == null && dialogTimer <= 0) {
            this.dialog = new Dialog(dialog);
            publish(this.dialog);
        }
    }

    /**
     * Publie un dialogue sur le joueur.
     *
     * @param dialog Instance du dialogue à afficher.
     */
    public void publish(Dialog dialog) {
        this.dialog = dialog;
    }

    /**
     * Associe une porte de manoir au joueur.
     *
     * @param manorDoor La porte de manoir.
     */
    private void setManorDoor(ManorDoor manorDoor) {
        this.manorDoor = manorDoor;
    }

    /**
     * Marque le joueur comme prêt à gagner le niveau.
     */
    public void setReadyToWin() {
        if (manorDoor != null) {
            manorDoor.setReadyToWin();
        }
    }

    /**
     * Déplace et oriente le joueur si un bouton est pressé.
     *
     * @param orientation Orientation souhaitée.
     * @param b           Bouton associé à cette orientation.
     * @return True si le joueur a été déplacé.
     */
    private boolean moveIfPressed(Orientation orientation, Button b) {
        if (b.isDown()) {
            if (!isDisplacementOccurs()) {
                orientate(orientation);
                move(MOVE_DURATION);
                return true;
            }
        }
        return false;
    }

    /**
     * Fait sortir le joueur de la zone actuelle.
     */
    public void leaveArea() {
        getOwnerArea().unregisterActor(this);
    }

    /**
     * Fait entrer le joueur dans une nouvelle zone à une position donnée.
     *
     * @param area     La zone à rejoindre.
     * @param position Coordonnées de la position d'entrée.
     */
    public void enterArea(Area area, DiscreteCoordinates position) {
        area.registerActor(this);
        area.setViewCandidate(this);
        setOwnerArea(area);
        setCurrentPosition(position.toVector());
        resetMotion();
    }

    /**
     * Recentre la caméra sur le joueur.
     */
    public void centerCamera() {
        getOwnerArea().setViewCandidate(this);
    }

    /**
     * Retourne le type d'élément associé au joueur.
     *
     * @return Le type d'élément ("FEU", "EAU", etc.).
     */
    public String getElementType() {
        return elementType;
    }

    /**
     * Attribut représentant la porte actuelle.
     */
    private Door currentDoor;

    /**
     * Attribut représentant le portail actuel.
     */
    private Portal currentPortal;

    /**
     * Attribut représentant le chemin de l'aire actuelle.
     */
    private String pathArea;

    /**
     * Attribut représentant la clé associée au joueur.
     */
    private Key key;

    /**
     * Liste des coordonnées associées au chemin de l'aire.
     */
    private List<DiscreteCoordinates> positionPathArea;

    /**
     * Traverse une porte spécifique.
     *
     * @param door             La porte à traverser.
     * @param pathArea         Le chemin de l'aire cible.
     * @param positionPathArea Les coordonnées de destination.
     */
    public void traverseDoor(Door door, String pathArea, List<DiscreteCoordinates> positionPathArea) {
        currentDoor = door;
        this.pathArea = pathArea;
        this.positionPathArea = positionPathArea;
    }

    /**
     * Traverse un portail spécifique.
     *
     * @param portal   Le portail à traverser.
     * @param pathArea Le chemin de l'aire cible.
     */
    public void traversePortal(Portal portal, String pathArea) {
        currentPortal = portal;
        this.pathArea = pathArea;
    }

    /**
     * Renvoie la porte actuelle.
     *
     * @return La porte actuelle, ou null si aucune.
     */
    public Door getCurrentDoor() {
        return currentDoor;
    }

    /**
     * Renvoie le portail actuel.
     *
     * @return Le portail actuel, ou null si aucun.
     */
    public Portal getCurrentPortal() {
        return currentPortal;
    }

    /**
     * Renvoie le chemin de l'aire actuelle.
     *
     * @return Le chemin de l'aire actuelle.
     */
    public String getPathArea() {
        return pathArea;
    }

    /**
     * Renvoie la liste des coordonnées associées au chemin de l'aire actuelle.
     *
     * @return Liste des coordonnées.
     */
    public List<DiscreteCoordinates> getPositionPathArea() {
        return positionPathArea;
    }

    /**
     * Gestionnaire interne pour les interactions du joueur.
     */
    private class ICoopPlayerInteractionHandler implements ICoopInteractionVisitor {
        private final ICoopPlayer player = ICoopPlayer.this;

        /**
         * Gère l'interaction avec une porte.
         * @param door La porte ciblée.
         * @param isCellInteraction True si l'interaction est cellulaire.
         */
        @Override
        public void interactWith(Door door, boolean isCellInteraction) {
            if (isCellInteraction && player.wantsCellInteraction()) {
                if (door.isOn()) {
                    traverseDoor(door, door.getPathArea(), door.getPositionPathArea());
                }
            }
        }

        /**
         * Gère l'interaction avec un portail.
         * @param portal Le portail ciblé.
         * @param isCellInteraction True si l'interaction est cellulaire.
         */
        @Override
        public void interactWith(Portal portal, boolean isCellInteraction) {
            if (isCellInteraction && player.wantsCellInteraction()) {
                if (portal.isOn()) {
                    traversePortal(portal, portal.getPathArea());
                }
            }
        }

        /**
         * Gère l'interaction avec une porte de manoir.
         * @param manorDoor La porte ciblée.
         * @param isCellInteraction True si l'interaction est cellulaire.
         */
        @Override
        public void interactWith(ManorDoor manorDoor, boolean isCellInteraction) {
            if (player.wantsCellInteraction() && isCellInteraction) {
                if (dialog == null) {
                    if (ICoopPlayer.this.manorDoor == null) setManorDoor(manorDoor);
                    else setActiveDialog(manorDoor.getManorDialog());
                }
            }
        }

        /**
         * Gère l'interaction avec un explosif.
         * @param bomb L'explosif ciblé.
         * @param isCellInteraction True si l'interaction est cellulaire.
         */
        @Override
        public void interactWith(Explosif bomb, boolean isCellInteraction) {
            if (player.wantsViewInteraction()) {
                if (!bomb.getExploded()) {
                    if (isCellInteraction && !bomb.getActive()) {
                        bomb.collect();
                        inventory.addItem(ICoopItem.EXPLOSIVE);
                    } else {
                        bomb.setActive(true);
                    }
                }
            }
        }

        /**
         * Gère l'interaction avec un orbe.
         * @param orb L'orbe ciblé.
         * @param isCellInteraction True si l'interaction est cellulaire.
         */
        @Override
        public void interactWith(Orb orb, boolean isCellInteraction) {
            if (player.wantsCellInteraction() && isCellInteraction) {
                if (player.getElementType().equals(orb.element())) {
                    orb.collect();
                    if (dialog == null) setActiveDialog(orb.getOrbDialog());
                    if (orb.element().equals("EAU")) player.invulnerabilityTimers.put(DamageType.WATER, 9999999.9f);
                    if (orb.element().equals("FEU")) player.invulnerabilityTimers.put(DamageType.FIRE, 9999999.9f);
                }
            }
        }

        /**
         * Gère l'interaction avec un coeur de vie.
         * @param heart Le coeur ciblé.
         * @param isCellInteraction True si l'interaction est cellulaire.
         */
        @Override
        public void interactWith(Heart heart, boolean isCellInteraction) {
            if (player.wantsCellInteraction() && isCellInteraction) {
                player.heal(3);
                heart.collect();
            }
        }

        /**
         * Gère l'interaction avec une plaque de pression.
         * @param plate La plaque ciblée.
         * @param isCellInteraction True si l'interaction est cellulaire.
         */
        @Override
        public void interactWith(PressurePlate plate, boolean isCellInteraction) {
            if (player.wantsCellInteraction() && isCellInteraction) {
                plate.setOff(player);
            }
        }

        /**
         * Gère l'interaction avec un autre joueur.
         * @param playerInteract Le joueur ciblé.
         * @param isCellInteraction True si l'interaction est cellulaire.
         */
        @Override
        public void interactWith(ICoopPlayer playerInteract, boolean isCellInteraction) {
            if (!isCellInteraction && wantsViewInteraction() && state == State.SWORD_ATTACK) {
                playerInteract.applyDamage(DamageType.PHYSICAL, 10);
            }
        }

        /**
         * Gère l'interaction avec un crâne infernal.
         * @param skull Le crâne ciblé.
         * @param isCellInteraction True si l'interaction est cellulaire.
         */
        @Override
        public void interactWith(HellSkull skull, boolean isCellInteraction) {
            if (wantsViewInteraction() && state == State.SWORD_ATTACK) {
                skull.applyDamage(DamageType.PHYSICAL, 1);
            }
        }

        /**
         * Gère l'interaction avec un ennemi explosif.
         * @param foe L'ennemi ciblé.
         * @param isCellInteraction True si l'interaction est cellulaire.
         */
        @Override
        public void interactWith(BombFoe foe, boolean isCellInteraction) {
            if (wantsViewInteraction() && state == State.SWORD_ATTACK) {
                foe.applyDamage(DamageType.PHYSICAL, 1);
            }
        }

        /**
         * Gère l'interaction avec un rocher destructible.
         * @param rock Le rocher ciblé.
         * @param isCellInteraction True si l'interaction est cellulaire.
         */
        @Override
        public void interactWith(Rock rock, boolean isCellInteraction) {
            if (wantsViewInteraction() && state == State.STAFF_ATTACK) {
                rock.setDestroyed(true);
            }
        }

        /**
         * Gère l'interaction avec un bâton élémentaire.
         * @param staff Le bâton ciblé.
         * @param isCellInteraction True si l'interaction est cellulaire.
         */
        @Override
        public void interactWith(Staff staff, boolean isCellInteraction) {
            if (player.wantsCellInteraction() && isCellInteraction) {
                if (player.getElementType().equals(staff.element())) {
                    staff.collect();
                    if (staff.element().equals("EAU")) inventory.addItem(ICoopItem.WATERSTAFF);
                    if (staff.element().equals("FEU")) inventory.addItem(ICoopItem.FIRESTAFF);
                }
            }
        }

        /**
         * Gère l'interaction avec une clé.
         * @param key La clé ciblée.
         * @param isCellInteraction True si l'interaction est cellulaire.
         */
        @Override
        public void interactWith(Key key, boolean isCellInteraction) {
            if (player.wantsCellInteraction() && isCellInteraction) {
                if (player.getElementType().equals(key.getElement())) {
                    setKey(key);
                    key.collect();
                }
            }
        }
    }
}