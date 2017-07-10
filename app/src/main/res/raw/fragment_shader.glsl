precision highp float;
uniform vec3 u_LightPos1,u_LightPos2,u_LightPos3,u_LightPos4;       	// The position of the light in eye space.
uniform vec3 u_LightDir1,u_LightDir2,u_LightDir3,u_LightDir4;       	// The position of the light in eye space.
float l_spotCutOff=45.0;
uniform sampler2D u_Texture;    // The input texture.
  
varying vec3 v_Position;		// Interpolated position for this fragment.
varying vec3 v_Normal;         	// Interpolated normal for this fragment.
varying vec2 v_TexCoordinate;   // Interpolated texture coordinate per fragment.
float cutoff = 0.1;
// The entry point for our fragment shader.
void main()                    		
{                              

	// Get a lighting direction vector from the light to the vertex.
    vec3 lightVector1 = normalize(u_LightPos1 - v_Position);
    vec3 lightVector2 = normalize(u_LightPos2 - v_Position);
    //vec3 lightVector3 = normalize(u_LightPos3 - v_Position);
    //vec3 lightVector4 = normalize(u_LightPos4 - v_Position);

    	// Will be used for attenuation.
        float distance1 = length(u_LightPos1 - v_Position)/1000.0;
    	float distance2 = length(u_LightPos2 - v_Position)/1000.0;
    	//float distance3 = length(u_LightPos3 - v_Position)/1000.0;
    	//float distance4 = length(u_LightPos4 - v_Position)/1000.0;

    float diffuse=0.0;

    	// Calculate the dot product of the light vector and vertex normal. If the normal and light vector are
    	// pointing in the same direction then it will get max illumination.
    float diffuse1 = max(dot(v_Normal, lightVector1), 0.1);
        // Add attenuation. (luminacity)
    diffuse1 = diffuse1 * (0.41/ (1.0+(0.025*distance1)));
    	// Calculate the dot product of the light vector and vertex normal. If the normal and light vector are
    	// pointing in the same direction then it will get max illumination.
    float diffuse2 = max(dot(v_Normal, lightVector2), 0.0);
        // Add attenuation. (luminacity)
    diffuse2 = diffuse2 * (0.41/ (1.0+(0.025*distance2)));

    // Add ambient lighting
    diffuse = diffuse1+diffuse2+0.7;//+diffuse3+diffuse4
	// Multiply the color by the diffuse illumination level and texture value to get final output color.
    vec4 color = (texture2D(u_Texture, v_TexCoordinate));
    color.rgb *= (diffuse);
    //color.rgb *= color.a;
    if( color.a < cutoff)
        discard;

    gl_FragColor = color;


  }                                                                     	

