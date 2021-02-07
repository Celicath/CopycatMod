package TheCopycat.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.WeakPower;

import static com.megacrit.cardcrawl.monsters.AbstractMonster.Intent.*;

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
			if (m.intent == ATTACK_DEBUFF || m.intent == DEBUFF || m.intent == STRONG_DEBUFF || m.intent == DEFEND_DEBUFF || m.intent == MAGIC) {
				addToTop(new ApplyPowerAction(m, AbstractDungeon.player, new WeakPower(m, magic, false)));
			}
			if (m.intent == ATTACK_BUFF || m.intent == BUFF || m.intent == DEFEND_BUFF || m.intent == MAGIC) {
				addToTop(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new StrengthPower(AbstractDungeon.player, magic)));
			}
			if (m.intent == ATTACK || m.intent == ATTACK_BUFF || m.intent == ATTACK_DEBUFF || m.intent == ATTACK_DEFEND || m.getIntentBaseDmg() >= 0) {
				addToTop(new DamageAction(m, info, AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
			}
			if (m.intent == ATTACK_DEFEND || m.intent == DEFEND || m.intent == DEFEND_DEBUFF || m.intent == DEFEND_BUFF) {
				addToTop(new GainBlockAction(AbstractDungeon.player, AbstractDungeon.player, block));
			}
		}
		isDone = true;
	}
}
