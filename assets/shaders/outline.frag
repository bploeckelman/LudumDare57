#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;
uniform float u_time;
uniform vec4 u_color1;

varying vec4 v_color;
varying vec2 v_texCoord;

const float range = 10.;


vec4 getTexture(vec2 p) {
    return texture2D(u_texture, p);
}

void main() {
    vec4 texColor = getTexture(v_texCoord);
    float outline = 0.;
    vec2 p = vec2(v_texCoord);
    if (texColor.a <= 0.) {
        for (float x = -range; x < range; x++) {
            for (float y = -range; y < range; y++) {
                p.x = v_texCoord.x + dFdx(v_texCoord.x) * x;
                p.y = v_texCoord.y + dFdy(v_texCoord.y) * y;
                outline = max(outline, getTexture(p).a);
            }
        }
    }


    texColor = mix(texColor, u_color1, outline);

    gl_FragColor = texColor * v_color;
}
