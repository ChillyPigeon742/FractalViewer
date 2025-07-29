#version 460 core

// Final output color for the pixel
out vec4 FragColor;

// Screen resolution (in pixels), passed in from CPU
uniform vec2 u_resolution;

// Inverse of max iterations, e.g., 1.0 / 100 = 0.01
// This is used to avoid dividing inside the loop — faster!
uniform float u_invMaxIter;

// Aspect ratio of the screen (width / height)
uniform float u_aspectRatio;

void main() {
    // Normalize screen coordinates to range [-1, 1]
    // gl_FragCoord is in pixel coordinates (e.g., [0, 1280])
    vec2 uv = (gl_FragCoord.xy / u_resolution) * 2.0 - vec2(1.0, 1.0);

    // Adjust X so the fractal doesn't stretch with screen size
    uv.x *= u_aspectRatio;

    // Map UV coordinates to complex plane
    // You can tweak the constants here to zoom/move the view
    vec2 c = uv * 1.5 - vec2(0.5, 0.0);

    // Start z at (0, 0), which is the origin in complex numbers
    vec2 z = vec2(0.0);

    // Convert inverse max iter to actual integer max
    int max_iter = int(1.0 / u_invMaxIter);
    int i = 0;

    // Mandelbrot iteration loop
    // If z escapes beyond radius 2 (length^2 > 4), break early
    for (; i < max_iter; i++) {
        if (dot(z, z) > 5.0) break;

        // z = z² + c
        // This is the core of the Mandelbrot set formula
        z = vec2(
        z.x * z.x - z.y * z.y + c.x,    // real part: x² - y² + cx
        2.0 * z.x * z.y + c.y           // imaginary part: 2xy + cy
        );
    }

    // Normalize iteration count (i) to range [0, 1]
    // Used to decide pixel brightness — 0 = black, 1 = white
    float t = float(i) * u_invMaxIter;

    // Final color: grayscale based on how fast the point escaped
    // Later you can replace this with color mapping logic
    FragColor = vec4(vec3(t), 1.0);
}