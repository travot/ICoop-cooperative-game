package ch.epfl.cs107.icoop.actor;


import ch.epfl.cs107.play.engine.actor.Graphics;
import ch.epfl.cs107.play.math.Positionable;
import ch.epfl.cs107.play.math.Transform;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.math.shape.Polygon;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;

import java.awt.Color;
import java.util.List;

/**
 * Classe Health.
 * Cette classe gère les points de vie d'une entité. Elle permet de dessiner une barre de vie sur une entité,
 * verte pour une entité amicale et rouge sinon. Elle implémente Graphics pour le rendu visuel et Logic
 * pour indiquer si l'entité a encore des points de vie ou non.
 */
public class Health implements Graphics, Logic {

    /**
     * Entité parente pour dessiner la barre de vie.
     */
    private final Positionable parent;

    /**
     * Transformation spécifique à chaque entité pour ajuster la taille de la barre de vie.
     */
    private final Transform transform;

    /**
     * Nombre maximum de points de vie que l'entité peut avoir.
     */
    private final int maxHealth;

    /**
     * Indique si l'entité est amicale (barre verte) ou hostile (barre rouge).
     */
    private final boolean isFriendly;

    /**
     * Nombre de points de vie actuels de l'entité.
     */
    private int healthPoints;

    /**
     * Constructeur par défaut de la classe Health.
     *
     * @param parent       Entité parente pour dessiner la barre de vie.
     * @param transform    Transformation associée à l'entité.
     * @param healthPoints Nombre initial de points de vie.
     * @param isFriendly   Indique si l'entité est amicale ou non.
     */
    public Health(Positionable parent, Transform transform, int healthPoints, boolean isFriendly) {
        this.parent = parent;
        this.transform = transform;
        this.maxHealth = healthPoints;
        this.healthPoints = healthPoints;
        this.isFriendly = isFriendly;
    }

    /**
     * Indique si l'entité a encore des points de vie.
     *
     * @return true si les points de vie sont supérieurs à 0, false sinon.
     */
    @Override
    public boolean isOn() {
        return healthPoints != 0;
    }

    /**
     * Indique si l'entité n'a plus de points de vie.
     *
     * @return true si les points de vie sont à 0, false sinon.
     */
    @Override
    public boolean isOff() {
        return !isOn();
    }

    /**
     * Renvoie l'intensité logique de l'entité.
     *
     * @return 1 si les points de vie sont supérieurs à 0, 0 sinon.
     */
    @Override
    public float getIntensity() {
        return isOn() ? 1 : 0;
    }

    /**
     * Dessine la barre de vie sur le canvas.
     *
     * @param canvas Le canvas sur lequel dessiner.
     */
    @Override
    public void draw(Canvas canvas) {
        // Dessine le contour gris
        canvas.drawShape(new Polygon(List.of(
                new Vector(0.1f, 0),
                new Vector(0.9f, 0),
                new Vector(0.9f, 0.15f),
                new Vector(0.1f, 0.15f)
        )), parent.getTransform().transformed(transform), Color.GRAY, Color.BLACK, 0.1f, 1, 0);

        // Dessine la barre verte ou rouge selon l'état de l'entité
        canvas.drawShape(new Polygon(List.of(
                new Vector(0.1f, 0),
                new Vector(0.8f * healthPoints / maxHealth + 0.1f, 0),
                new Vector(0.8f * healthPoints / maxHealth + 0.1f, 0.15f),
                new Vector(0.1f, 0.15f)
        )), parent.getTransform().transformed(transform), isFriendly ? Color.GREEN : Color.RED, null, 0, 0.5f, 0);
    }

    /**
     * Augmente les points de vie de l'entité.
     *
     * @param amount Nombre de points à ajouter. Limité à maxHealth.
     */
    public void increase(int amount) {
        healthPoints = Math.min(maxHealth, healthPoints + amount);
    }

    /**
     * Réduit les points de vie de l'entité.
     *
     * @param amount Nombre de points à retirer. Minimum à 0.
     */
    public void decrease(int amount) {
        healthPoints = Math.max(0, healthPoints - amount);
    }

    /**
     * Réinitialise les points de vie de l'entité à leur maximum.
     */
    public void resetHealth() {
        healthPoints = maxHealth;
    }
}

