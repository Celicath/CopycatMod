package TheCopycat.actions;

import TheCopycat.utils.GameLogicUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.WeakPower;

public class MeFirstAction extends AbstractGameAction {
	AbstractMonster m;
	DamageInfo info;
	int block, magic;

	public MeFirstAction(AbstractMonster m, DamageInfo info, int block, int magicNumber) {
		this.m = m;
		this.info = info;
		this.block = block;
		this.magic = magicNumber;
	}

	@Override
	public void update() {
		if (m != null) {
			if (GameLogicUtils.checkIntent(m, 3)) {
				addToTop(new ApplyPowerAction(m, AbstractDungeon.player, new WeakPower(m, magic, false)));
			}
			if (GameLogicUtils.checkIntent(m, 2)) {
				addToTop(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new StrengthPower(AbstractDungeon.player, magic)));
			}
			if (GameLogicUtils.checkIntent(m, 1)) {
				addToTop(new DamageAction(m, info, AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
			}
			if (GameLogicUtils.checkIntent(m, 0)) {
				addToTop(new GainBlockAction(AbstractDungeon.player, AbstractDungeon.player, block));
			}
		}
		isDone = true;
	}
}
