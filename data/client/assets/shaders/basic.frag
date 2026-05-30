#version 440 core

layout(location=0) out vec4 color;

in vec4 v_Color;
in vec2 v_TexCoord;
in float v_TexIndex;
in float v_TileFactor;

uniform sampler2D u_Textures[32];

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
        case  0: texColor *= texture(u_Textures[ 0], texCoord * v_TileFactor) * v_Color; break;
        case  1: texColor *= texture(u_Textures[ 1], texCoord * v_TileFactor) * v_Color; break;
        case  2: texColor *= texture(u_Textures[ 2], texCoord * v_TileFactor) * v_Color; break;
        case  3: texColor *= texture(u_Textures[ 3], texCoord * v_TileFactor) * v_Color; break;
        case  4: texColor *= texture(u_Textures[ 4], texCoord * v_TileFactor) * v_Color; break;
        case  5: texColor *= texture(u_Textures[ 5], texCoord * v_TileFactor) * v_Color; break;
        case  6: texColor *= texture(u_Textures[ 6], texCoord * v_TileFactor) * v_Color; break;
        case  7: texColor *= texture(u_Textures[ 7], texCoord * v_TileFactor) * v_Color; break;
        case  8: texColor *= texture(u_Textures[ 8], texCoord * v_TileFactor) * v_Color; break;
        case  9: texColor *= texture(u_Textures[ 9], texCoord * v_TileFactor) * v_Color; break;
        case 10: texColor *= texture(u_Textures[10], texCoord * v_TileFactor) * v_Color; break;
        case 11: texColor *= texture(u_Textures[11], texCoord * v_TileFactor) * v_Color; break;
        case 12: texColor *= texture(u_Textures[12], texCoord * v_TileFactor) * v_Color; break;
        case 13: texColor *= texture(u_Textures[13], texCoord * v_TileFactor) * v_Color; break;
        case 14: texColor *= texture(u_Textures[14], texCoord * v_TileFactor) * v_Color; break;
        case 15: texColor *= texture(u_Textures[15], texCoord * v_TileFactor) * v_Color; break;
        case 16: texColor *= texture(u_Textures[16], texCoord * v_TileFactor) * v_Color; break;
        case 17: texColor *= texture(u_Textures[17], texCoord * v_TileFactor) * v_Color; break;
        case 18: texColor *= texture(u_Textures[18], texCoord * v_TileFactor) * v_Color; break;
        case 19: texColor *= texture(u_Textures[19], texCoord * v_TileFactor) * v_Color; break;
        case 20: texColor *= texture(u_Textures[20], texCoord * v_TileFactor) * v_Color; break;
        case 21: texColor *= texture(u_Textures[21], texCoord * v_TileFactor) * v_Color; break;
        case 22: texColor *= texture(u_Textures[22], texCoord * v_TileFactor) * v_Color; break;
        case 23: texColor *= texture(u_Textures[23], texCoord * v_TileFactor) * v_Color; break;
        case 24: texColor *= texture(u_Textures[24], texCoord * v_TileFactor) * v_Color; break;
        case 25: texColor *= texture(u_Textures[25], texCoord * v_TileFactor) * v_Color; break;
        case 26: texColor *= texture(u_Textures[26], texCoord * v_TileFactor) * v_Color; break;
        case 27: texColor *= texture(u_Textures[27], texCoord * v_TileFactor) * v_Color; break;
        case 28: texColor *= texture(u_Textures[28], texCoord * v_TileFactor) * v_Color; break;
        case 29: texColor *= texture(u_Textures[29], texCoord * v_TileFactor) * v_Color; break;
        case 30: texColor *= texture(u_Textures[30], texCoord * v_TileFactor) * v_Color; break;
        case 31: texColor *= texture(u_Textures[31], texCoord * v_TileFactor) * v_Color; break;
    }

    color = transparentKey(texColor);
}