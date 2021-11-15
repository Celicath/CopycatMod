package TheCopycat.utils;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class Shader {
	static String vertexShader = "attribute vec4 a_position;\n" +
		"attribute vec4 a_color;\n" +
		"attribute vec2 a_texCoord0;\n" +
		"uniform mat4 u_projTrans;\n" +
		"varying vec4 v_color;\n" +
		"varying vec2 v_texCoords;\n" +
		"void main() {\n" +
		"    v_color = a_color;\n" +
		"    v_texCoords = a_texCoord0;\n" +
		"    gl_Position = u_projTrans * a_position;\n" +
		"}";

	static String redFragmentShader = "#ifdef GL_ES\n" +
		"    precision mediump float;\n" +
		"#endif\n" +
		"\n" +
		"varying vec4 v_color;\n" +
		"varying vec2 v_texCoords;\n" +
		"uniform sampler2D u_texture;\n" +
		"uniform vec2 u_resolution;\n" +
		"\n" +
		"void main() {\n" +
		"  vec2 st = gl_FragCoord.xy/vec2(500.0, 380.0f);\n" +
		"  vec4 c = texture2D(u_texture, v_texCoords);\n" +
		"  vec4 color1 = vec4(1.0, 1.0, 1.0, c.a);\n" +
		"  vec4 color2 = vec4((c.r + 0.4) / 1.4, c.g * 0.7, c.b * 0.7, c.a);\n" +
		"  float a = 0.6 + distance(st, vec2(v_color.r, v_color.g)) * 0.4;\n" +
		"  gl_FragColor = mix(color1, color2, a);\n" +
		"}";
	/**
	 * Tints the color to Red and do gradient on it.
	 * Set Color.R and Color.G to Gradient center position
	 */
	public static ShaderProgram redGradientShader = new ShaderProgram(vertexShader, redFragmentShader);
	static String greenFragmentShader = "#ifdef GL_ES\n" +
		"    precision mediump float;\n" +
		"#endif\n" +
		"\n" +
		"varying vec4 v_color;\n" +
		"varying vec2 v_texCoords;\n" +
		"uniform sampler2D u_texture;\n" +
		"uniform vec2 u_resolution;\n" +
		"\n" +
		"void main() {\n" +
		"  vec2 st = gl_FragCoord.xy/vec2(500.0, 380.0f);\n" +
		"  vec4 c = texture2D(u_texture, v_texCoords);\n" +
		"  vec4 color1 = vec4(1.0, 1.0, 1.0, c.a);\n" +
		"  vec4 color2 = vec4(c.r * 0.7, (c.g + 0.4) / 1.4, c.b * 0.7, c.a);\n" +
		"  float a = 0.6 + distance(st, vec2(v_color.r, v_color.g)) * 0.4;\n" +
		"  gl_FragColor = mix(color1, color2, a);\n" +
		"}";
	/**
	 * Tints the color to Green and do gradient on it.
	 * Set Color.R and Color.G to Gradient center position
	 */
	public static ShaderProgram greenGradientShader = new ShaderProgram(vertexShader, greenFragmentShader);
	static String blueFragmentShader = "#ifdef GL_ES\n" +
		"    precision mediump float;\n" +
		"#endif\n" +
		"\n" +
		"varying vec4 v_color;\n" +
		"varying vec2 v_texCoords;\n" +
		"uniform sampler2D u_texture;\n" +
		"uniform vec2 u_resolution;\n" +
		"\n" +
		"void main() {\n" +
		"  vec2 st = gl_FragCoord.xy/vec2(500.0, 380.0f);\n" +
		"  vec4 c = texture2D(u_texture, v_texCoords);\n" +
		"  vec4 color1 = vec4(1.0, 1.0, 1.0, c.a);\n" +
		"  vec4 color2 = vec4(c.r * 0.7, c.g * 0.7, (c.b + 0.4) / 1.4, c.a);\n" +
		"  float a = 0.6 + distance(st, vec2(v_color.r, v_color.g)) * 0.4;\n" +
		"  gl_FragColor = mix(color1, color2, a);\n" +
		"}";
	/**
	 * Tints the color to Blue and do gradient on it.
	 * Set Color.R and Color.G to Gradient center position
	 */
	public static ShaderProgram blueGradientShader = new ShaderProgram(vertexShader, blueFragmentShader);
	static String tintFragmentShader = "#ifdef GL_ES\n" +
		"    precision mediump float;\n" +
		"#endif\n" +
		"\n" +
		"varying vec4 v_color;\n" +
		"varying vec2 v_texCoords;\n" +
		"uniform sampler2D u_texture;\n" +
		"\n" +
		"void main() {\n" +
		"  vec4 color1 = texture2D(u_texture, v_texCoords);\n" +
		"  vec4 color2 = vec4(v_color.r * (1.0 - color1.r), v_color.g * (1.0 - color1.g), v_color.b * (1.0 - color1.b), 0);\n" +
		"  gl_FragColor = color1 + color2 * 0.25;\n" +
		"}";
	/**
	 * Tints the color to Color.
	 */
	public static ShaderProgram tintGradientShader = new ShaderProgram(vertexShader, tintFragmentShader);
}
