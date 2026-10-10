package ch.epfl.cs107.icoop.handler;

import ch.epfl.cs107.icoop.ICoopBehavior;
import ch.epfl.cs107.icoop.actor.*;
import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;

/**
 * InteractionVisitor for the ICoop entities
 */

public interface ICoopInteractionVisitor extends AreaInteractionVisitor {

    default void interactWith(ICoopPlayer player, boolean isCellInteraction) {
    }

    default void interactWith(ICoopBehavior.ICoopCell cell, boolean isCellInteraction) {
    }

    default void interactWith(Door door, boolean isCellInteraction) {
    }

    default void interactWith(Explosif bomb, boolean isCellInteraction) {
    }

    default void interactWith(Obstacle obstacle, boolean isCellInteraction) {
    }

    default void interactWith(Orb orb, boolean isCellInteraction) {
    }

    default void interactWith(Heart heart, boolean isCellInteraction) {
    }

    default void interactWith(Wall wall, boolean isCellInteraction) {
    }

    default void interactWith(PressurePlate plate, boolean isCellInteraction) {
    }

    default void interactWith(HellSkull skull, boolean isCellInteraction) {
    }

    default void interactWith(BombFoe foe, boolean isCellInteraction) {
    }

    default void interactWith(Staff staff, boolean isCellInteraction) {
    }

    default void interactWith(Key key, boolean isCellInteraction) {
    }

    default void interactWith(Portal portal, boolean isCellInteraction) {
    }

    default void interactWith(ManorDoor manorDoor, boolean isCellInteraction) {
    }

    default void interactWith(Rock rock, boolean isCellInteraction) {
    }
}
