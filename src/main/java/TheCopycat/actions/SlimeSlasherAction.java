package TheCopycat.actions;

import TheCopycat.cards.SlimeSlasher;
import TheCopycat.friendlyminions.MirrorMinion;
import TheCopycat.utils.BetterFriendlyMinionsUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.mod.stslib.StSLib;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.exordium.*;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.TextAboveCreatureEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;

import java.util.stream.Collectors;

public class SlimeSlasherAction extends AbstractGameAction {
	SlimeSlasher c;
	int phase = 0;
	int fatalCount = 0;

	public SlimeSlasherAction(SlimeSlasher c) {
		this.c = c;
		this.startDuration = Settings.ACTION_DUR_FAST;
		this.duration = startDuration;
		this.damageType = c.damageTypeForTurn;
		this.actionType = ActionType.DAMAGE;
	}

	public void update() {
		if (phase == 0) {
			boolean playedMusic = false;

			for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
				if (!m.isDying && m.currentHealth > 0 && !m.isEscaping) {
					AbstractDungeon.effectList.add(new FlashAtkImgEffect(m.hb.cX, m.hb.cY, AttackEffect.SLASH_HORIZONTAL, playedMusic));
					playedMusic = true;
				}
			}
			phase = 1;
		} else if (phase == 2) {
			boolean playedMusic = false;

			for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
				if (!m.isDying && m.currentHealth > 0 && !m.isEscaping && isSlime(m)) {
					AbstractDungeon.effectList.add(new FlashAtkImgEffect(m.hb.cX, m.hb.cY, AttackEffect.SLASH_VERTICAL, playedMusic));
					playedMusic = true;
				}
			}
			for (AbstractMonster m : BetterFriendlyMinionsUtils.getMinionList()) {
				if (isAllySlime(m)) {
					AbstractDungeon.effectList.add(new FlashAtkImgEffect(m.hb.cX, m.hb.cY, AttackEffect.SLASH_VERTICAL, playedMusic));
					playedMusic = true;
				}
			}
			if (!playedMusic) {
				isDone = true;
			}
			phase = 3;
		}

		tickDuration();
		if (isDone) {
			if (phase == 1) {
				phase++;
				isDone = false;
				duration = startDuration;
				for (AbstractPower p : AbstractDungeon.player.powers) {
					p.onDamageAllEnemies(c.multiDamage);
				}

				int index = 0;
				for (int i = 0; i < AbstractDungeon.getCurrRoom().monsters.monsters.size(); i++) {
					AbstractMonster m = AbstractDungeon.getCurrRoom().monsters.monsters.get(i);
					if (!m.isDeadOrEscaped()) {
						damageMonster(m, new DamageInfo(source, c.multiDamage[index], damageType));
					}
				}
			} else {
				for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
					if (!m.isDeadOrEscaped() && isSlime(m)) {
						damageMonster(m, new DamageInfo(source, c.calculateSingleTargetDamage(m), damageType));
					}
				}

				for (AbstractMonster m : BetterFriendlyMinionsUtils.getMinionList().stream().filter(SlimeSlasherAction::isAllySlime).collect(Collectors.toList())) {
					damageMonster(m, new DamageInfo(source, c.calculateSingleTargetDamage(m), damageType));
				}

				updateFatal();
			}
		}
	}

	private void damageMonster(AbstractMonster m, DamageInfo damageInfo) {
		m.damage(damageInfo);
		if (m.isDying && (isSlime(m) || isAllySlime(m))) {
			fatalCount++;
		}
	}

	public static boolean isSlime(AbstractMonster m) {
		return m instanceof ApologySlime ||
				m instanceof SlimeBoss ||
				m instanceof SpikeSlime_S ||
				m instanceof SpikeSlime_M ||
				m instanceof SpikeSlime_L ||
				m instanceof AcidSlime_S ||
				m instanceof AcidSlime_M ||
				m instanceof AcidSlime_L;
	}

	public static boolean isAllySlime(AbstractMonster m) {
		return m instanceof MirrorMinion && isSlime(((MirrorMinion) m).origMonster);
	}

	public void updateFatal() {
		c.misc += fatalCount;
		c.recalculateDamage();
		AbstractCard masterCard = StSLib.getMasterDeckEquivalent(c);
		if (!(masterCard instanceof SlimeSlasher)) {
			masterCard = c;
		}
		AbstractCard effectCard = masterCard.makeStatEquivalentCopy();
		masterCard.misc += fatalCount;

		((SlimeSlasher) masterCard).recalculateDamage();
		if (masterCard.baseDamage > effectCard.baseDamage) {
			masterCard.superFlash();
			final int startDamage = effectCard.baseDamage;
			final int targetDamage = masterCard.baseDamage;

			AbstractDungeon.topLevelEffects.add(new ShowCardBrieflyEffect(effectCard) {
				{
					duration = startingDuration *= 1.2f;
				}

				@Override
				public void update() {
					super.update();
					float t = 1.0f - (duration / startingDuration);
					if (t < 0.2f) t = 0;
					else if (t > 0.8f) t = 1;
					else t = (t - 0.2f) / 0.6f;
					float d = Interpolation.fade.apply(startDamage, targetDamage, t);
					effectCard.baseDamage = MathUtils.round(d);
				}
			});
			AbstractDungeon.topLevelEffects.add(new TextAboveCreatureEffect(
					Settings.WIDTH / 2.0f,
					Settings.HEIGHT / 2.0f - 120.0f * Settings.scale,
					"+" + (masterCard.baseDamage - effectCard.baseDamage),
					Color.LIME) {
				{
					duration += 0.5f;
				}

				@Override
				public void update() {
					if (duration >= startingDuration) {
						this.duration -= Gdx.graphics.getDeltaTime();
					} else {
						super.update();
					}
				}

				@Override
				public void render(SpriteBatch sb) {
					if (duration < startingDuration) {
						super.render(sb);
					}
				}
			});
		}
	}
}
