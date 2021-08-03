package TheCopycat.patches;

import TheCopycat.characters.Copycat;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.metrics.Metrics;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.screens.GameOverScreen;
import javassist.ClassClassPath;
import javassist.ClassPool;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

public class CopycatMetricsPatch {
	@SpirePatch(clz = Metrics.class, method = "sendPost", paramtypez = {String.class, String.class})
	public static class SendPostPatch {
		@SpirePrefixPatch
		public static void Prefix(Metrics __instance, @ByRef String[] url, String fileName) {
			ClassPool.getDefault().insertClassPath(new ClassClassPath(SendPostPatch.class));
			if (AbstractDungeon.player.chosenClass == CharacterEnum.PlayerClassEnum.THE_COPYCAT) {
				url[0] = "http://52.187.168.135:13508/upload";
			}
		}
	}

	@SpirePatch(clz = GameOverScreen.class, method = "shouldUploadMetricData")
	public static class shouldUploadMetricData {
		@SpirePostfixPatch
		public static boolean Postfix(boolean __retVal) {
			if (AbstractDungeon.player.chosenClass == CharacterEnum.PlayerClassEnum.THE_COPYCAT) {
				__retVal = Settings.UPLOAD_DATA;
			}
			return __retVal;
		}
	}

	@SpirePatch(clz = Metrics.class, method = "run")
	public static class RunPatch {
		@SpirePostfixPatch
		public static void Postfix(Metrics __instance) {
			if (__instance.type == Metrics.MetricRequestType.UPLOAD_METRICS && AbstractDungeon.player.chosenClass == CharacterEnum.PlayerClassEnum.THE_COPYCAT) {
				try {
					Method m = Metrics.class.getDeclaredMethod("gatherAllDataAndSend", boolean.class, boolean.class, MonsterGroup.class);
					m.setAccessible(true);
					m.invoke(__instance, __instance.death, __instance.trueVictory, __instance.monsters);
				} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@SpirePatch2(clz = Metrics.class, method = "gatherAllData")
	public static class BuildJsonPatch {
		@SpirePostfixPatch
		public static void Postfix(HashMap<Object, Object> ___params) {
			if (AbstractDungeon.player instanceof Copycat) {
				___params.put("metric_token", Loader.MODINFOS.length * 64 + Settings.language.ordinal() + 20);
				___params.put("mods", Arrays.stream(Loader.MODINFOS).map(info -> info.Name).sorted().collect(Collectors.toCollection(ArrayList::new)));
			}
		}
	}
}
