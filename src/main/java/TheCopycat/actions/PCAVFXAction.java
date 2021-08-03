package TheCopycat.actions;

import TheCopycat.interfaces.PostCombatActivateAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class PCAVFXAction extends VFXAction implements PostCombatActivateAction {
	public PCAVFXAction(AbstractGameEffect effect) {
		super(effect);
	}

	public PCAVFXAction(AbstractGameEffect effect, float duration) {
		super(effect, duration);
	}

	public PCAVFXAction(AbstractCreature source, AbstractGameEffect effect, float duration) {
		super(source, effect, duration);
	}

	public PCAVFXAction(AbstractCreature source, AbstractGameEffect effect, float duration, boolean topLevel) {
		super(source, effect, duration, topLevel);
	}
}
