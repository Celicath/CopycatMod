package TheCopycat.interfaces;

/**
 * Card which targets allies.
 * If you set CardTarget to CardTarget.ENEMY, it will make you target an ally by dragging the card onto it.
 * If you set CardTarget to any other, renderReticle will be called on all allies when you try to use the card.
 */
public interface TargetAllyCard {
}
