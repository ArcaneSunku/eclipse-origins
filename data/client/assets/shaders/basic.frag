#version 150 core

out vec4 color;

in vec4 v_Color;
in vec2 v_TexCoord;
in float v_TexIndex;
in float v_TileFactor;

uniform sampler2D u_Textures[{{MAX_TEXTURE_SLOTS}}];

// Integral to using transparency with BMP
vec4 transparentKey(vec4 testColor)
{
    if((testColor.r == 1 && testColor.g == 0 && testColor.b == 1) || (testColor.r == 0 && testColor.g == 1 && testColor.b == 0))
    return vec4(testColor.rgb, 0);

    return testColor;
}

void main()
{
    vec4 texColor = vec4(1, 1, 1, 1);
    vec2 texCoord = v_TexCoord;// + 0.0001;

    switch(int(v_TexIndex))
    {
{{SAMPLER_SWITCH_CASES}}
    }

    color = transparentKey(texColor);
}
