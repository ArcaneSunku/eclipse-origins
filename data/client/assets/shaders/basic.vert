#version 150 core

in vec3 a_Position;
in vec4 a_Color;
in vec2 a_TexCoord;
in float a_TexIndex;
in float a_TileFactor;

uniform mat4 u_ViewProjection;

out vec4 v_Color;
out vec2 v_TexCoord;
out float v_TexIndex;
out float v_TileFactor;

void main()
{
    v_Color = a_Color;

    v_TexCoord = a_TexCoord;
    v_TexIndex = a_TexIndex;
    v_TileFactor = a_TileFactor;

    gl_Position = u_ViewProjection * vec4(a_Position, 1.0);
}
