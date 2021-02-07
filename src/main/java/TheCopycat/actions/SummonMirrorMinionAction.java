package TheCopycat.actions;

import TheCopycat.CopycatModMain;
import TheCopycat.friendlyminions.MirrorMinion;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.vfx.ThoughtBubble;
import kobting.friendlyminions.helpers.BasePlayerMinionHelper;
import kobting.friendlyminions.monsters.AbstractFriendlyMonster;
import kobting.friendlyminions.patches.PlayerAddFieldsPatch;

public class SummonMirrorMinionAction extends AbstractGameAction {
	private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(CopycatModMain.makeID("Minion"));
	public static final String[] TEXT = uiStrings.TEXT;

	String name;
	int maxHealth;
	AbstractMonster m;
	AbstractFriendlyMonster[] monsters = new AbstractFriendlyMonster[4];

	public SummonMirrorMinionAction(String name, AbstractMonster m, int maxHealth) {
		actionType = ActionType.POWER;
		duration = Settings.ACTION_DUR_FAST;
		this.name = name;
		this.maxHealth = maxHealth;
		this.m = m;
	}

	public void update() {
		if (PlayerAddFieldsPatch.f_maxMinions.get(AbstractDungeon.player) == 1) {
			PlayerAddFieldsPatch.f_maxMinions.set(AbstractDungeon.player, 4);
		}
		MonsterGroup group = PlayerAddFieldsPatch.f_minions.get(AbstractDungeon.player);

		int index = -1;
		for (int i = 0; i < monsters.length; i++) {
			if (monsters[i] != null && group.monsters.contains(monsters[i])) {
				continue;
			}
			index = i;
			break;
		}
		if (index == -1) {
			AbstractDungeon.effectsQueue.add(new ThoughtBubble(AbstractDungeon.player.dialogX, AbstractDungeon.player.dialogY, 3.0F, TEXT[0], true));
		} else {
			float offsetX = index >= 2 ? -800.0f : -1200.0f;
			float offsetY = index == 0 || index == 3 ? 0.0f : 400.0f;
			MirrorMinion mm = new MirrorMinion(name, m, maxHealth, offsetX, offsetY);
			BasePlayerMinionHelper.addMinion(AbstractDungeon.player, mm);
			monsters[index] = mm;
		}
		isDone = true;
	}
}
