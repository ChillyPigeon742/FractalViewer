#version 460 core
out vec4 FragColor;
uniform vec2 u_resolution;
uniform float u_invMaxIter;

void main() {
    vec2 uv = (gl_FragCoord.xy / u_resolution) * 3.0 - vec2(2.0, 1.5);

    vec2 c = uv;
    vec2 z = vec2(0.0);
    int max_iter = int(1.0 / u_invMaxIter);
    int i = 0;

    for (; i < max_iter; i++) {
        if (dot(z, z) > 4.0) break;
        z = vec2(
        z.x * z.x - z.y * z.y + c.x,
        2.0 * z.x * z.y + c.y
        );
    }

    float t = float(i) * u_invMaxIter;
    FragColor = vec4(vec3(t), 1.0);
}