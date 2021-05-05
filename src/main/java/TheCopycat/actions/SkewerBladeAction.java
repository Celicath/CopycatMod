package TheCopycat.actions;

import TheCopycat.cards.SkewerBlade;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.ChemicalX;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class SkewerBladeAction extends AbstractGameAction {
	private boolean freeToPlayOnce;
	private int damage;
	private AbstractPlayer p;
	private AbstractMonster m;
	private DamageInfo.DamageType damageTypeForTurn;
	private int energyOnUse;
	SkewerBlade c;

	public SkewerBladeAction(SkewerBlade c, AbstractPlayer p, AbstractMonster m, int damage, DamageInfo.DamageType damageTypeForTurn, boolean freeToPlayOnce, int energyOnUse) {
		this.c = c;
		this.p = p;
		this.m = m;
		this.damage = damage;
		this.freeToPlayOnce = freeToPlayOnce;
		this.duration = Settings.ACTION_DUR_XFAST;
		this.actionType = ActionType.SPECIAL;
		this.damageTypeForTurn = damageTypeForTurn;
		this.energyOnUse = energyOnUse;
	}

	public void update() {
		int effect = EnergyPanel.totalCount;
		if (energyOnUse != -1) {
			effect = energyOnUse;
		}

		AbstractRelic r = p.getRelic(ChemicalX.ID);
		if (r != null) {
			effect += 2;
			r.flash();
		}

		int finalEffect = effect;
		addToTop(new AbstractGameAction() {
			{
				duration = startDuration = 0.4f;
			}

			@Override
			public void update() {
				if (duration == startDuration) {
					c.fixEffect(finalEffect);
				}
				tickDuration();
			}
		});

		if (effect > 0) {
			for (int i = 0; i < effect; ++i) {
				addToTop(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AttackEffect.BLUNT_LIGHT));
			}

			if (!freeToPlayOnce) {
				p.energy.use(EnergyPanel.totalCount);
			}
		}

		isDone = true;
	}
}
