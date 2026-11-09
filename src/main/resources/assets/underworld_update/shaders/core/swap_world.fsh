#version 150

uniform sampler2D TerrainDepth;   // profondeur du terrain seul
uniform sampler2D EntityDepth;    // profondeur terrain + entités
uniform sampler2D SceneColor;     // copie de l'image du jeu
uniform mat4 InvViewProj;
uniform vec4 Params;              // x = temps (s), y = rayon de l'onde sortante, z = rayon de l'onde de retour
uniform vec4 WaveC;               // xyz = centre de l'onde, relatif à la caméra
uniform vec4 HoleA;               // xyz = centre du portail, relatif à la caméra, w = rayon (blocs)
uniform vec4 HoleB;

in vec2 texCoord;
out vec4 fragColor;

// ---- Réglages ----
const float WORLD_BRIGHTNESS = 0.30;           // luminosité du monde en niveaux de gris (0 = noir total)
const vec3 LINE_COL = vec3(0.90);              // couleur des bordures
const float LINE_SENSITIVITY = 0.0007;         // plus petit = plus de bordures
const vec3 VIOLET = vec3(0.42, 0.10, 0.95);
const vec3 HOT = vec3(1.00, 0.92, 1.00);

vec3 pointFrom(vec2 uv, float z) {
    vec4 q = InvViewProj * vec4(uv * 2.0 - 1.0, z * 2.0 - 1.0, 1.0);
    return q.xyz / q.w;
}

vec3 terrainPoint(vec2 uv) {
    return pointFrom(uv, texture(TerrainDepth, uv).r);
}

// Bordures : on cherche les endroits où la surface n'est plus un plan (arêtes, silhouettes).
float outline(vec3 c) {
    vec2 px = 1.0 / vec2(textureSize(TerrainDepth, 0));
    vec3 l = terrainPoint(texCoord - vec2(px.x, 0.0));
    vec3 r = terrainPoint(texCoord + vec2(px.x, 0.0));
    vec3 u = terrainPoint(texCoord + vec2(0.0, px.y));
    vec3 d = terrainPoint(texCoord - vec2(0.0, px.y));
    vec3 n = cross(r - l, u - d);
    float len = length(n);
    if (len < 1e-9) return 0.0;
    n /= len;
    float dist = length(c);
    float res = abs(dot(l + r + u + d - 4.0 * c, n));
    float thr = LINE_SENSITIVITY * dist + 0.0004;
    float e = smoothstep(thr, thr * 3.0, res);
    return e * (1.0 - smoothstep(90.0, 220.0, dist));
}

float ringAt(float dist, float front, float w) {
    float x = (dist - front) / w;
    return exp(-x * x);
}

// Trou noir plaqué sur le sol : horizon noir, anneau de photons, disque d'accrétion en spirale.
vec4 holeDecal(vec3 P, vec4 H, float time) {
    float R = H.w;
    if (R < 0.02) return vec4(0.0);
    vec2 d = P.xz - H.xz;
    float u = length(d) / R;
    if (u >= 1.0) return vec4(0.0);
    float onGround = 1.0 - smoothstep(1.2, 2.6, abs(P.y - H.y));
    if (onGround <= 0.0) return vec4(0.0);

    float a = atan(d.y, d.x) + 3.2 * log(max(u, 0.03)) - time * 3.5;
    float arms = pow(0.5 + 0.5 * sin(a * 3.0), 2.0);

    const float HORIZON = 0.46;
    float core = 1.0 - smoothstep(HORIZON - 0.03, HORIZON, u);
    float disc = smoothstep(HORIZON - 0.02, HORIZON + 0.06, u) * (1.0 - smoothstep(0.78, 1.0, u));
    float pr = (u - HORIZON - 0.02) / 0.025;
    float photon = exp(-pr * pr);

    vec3 col = mix(VIOLET, HOT, arms * (1.0 - u));
    col = mix(col, HOT, photon);
    float alpha = clamp(core + disc * (0.25 + 0.75 * arms) + photon, 0.0, 1.0);
    return vec4(col * (1.0 - core), alpha * onGround);
}

void main() {
    float tD = texture(TerrainDepth, texCoord).r;
    float eD = texture(EntityDepth, texCoord).r;
    bool isEntity = eD < tD;
    bool isSky = !isEntity && tD >= 0.99999;
    float time = Params.x;
    float outer = Params.y;
    float inner = Params.z;

    // Distance de ce pixel au centre de l'onde. Le ciel n'a pas de position : on lui donne une
    // distance qui augmente avec la hauteur, l'onde monte donc dans le ciel depuis l'horizon.
    vec3 P = pointFrom(texCoord, tD);
    float dist;
    if (isEntity) {
        dist = distance(pointFrom(texCoord, eD), WaveC.xyz);
    } else if (isSky) {
        vec3 dir = normalize(P);
        dist = 50.0 + 90.0 * clamp(abs(dir.y) * 1.5, 0.0, 1.0);
    } else {
        dist = distance(P, WaveC.xyz);
    }

    // Masque de l'onde : actif derrière le front sortant, inactif derrière le front de retour.
    float w = 1.2 + 0.012 * dist;
    float mask = (1.0 - smoothstep(outer - w, outer, dist)) * smoothstep(inner - w, inner, dist);

    // Anneaux lumineux sur les deux fronts.
    float ring = 0.0;
    if (outer < 1.0e5) ring += ringAt(dist, outer, w * 1.6) * (1.0 - 0.6 * smoothstep(100.0, 260.0, outer));
    if (inner > 0.5 && inner < 1.0e5) ring += ringAt(dist, inner, w * 1.6) * (1.0 - 0.6 * smoothstep(100.0, 260.0, inner));
    ring = clamp(ring, 0.0, 1.0);

    if (mask < 0.003 && ring < 0.003) discard;

    vec3 rgb;
    if (isEntity) {
        rgb = vec3(1.0);   // entités : blanches
    } else {
        // Monde : vision monochrome sombre + bordures claires.
        float lum = dot(texture(SceneColor, texCoord).rgb, vec3(0.299, 0.587, 0.114));
        rgb = vec3(pow(lum, 1.3) * WORLD_BRIGHTNESS);
        rgb = mix(rgb, LINE_COL, outline(P));

        // Portails sur le sol.
        if (!isSky) {
            vec4 a = holeDecal(P, HoleA, time);
            vec4 b = holeDecal(P, HoleB, time);
            rgb = mix(rgb, a.rgb, a.a);
            rgb = mix(rgb, b.rgb, b.a);
        }
    }

    rgb = mix(rgb, mix(VIOLET, HOT, ring), ring);
    fragColor = vec4(rgb, max(mask, ring * 0.85));
}
