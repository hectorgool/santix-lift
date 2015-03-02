//usar rutas completas a partir del archivo Gruntfile.js
module.exports = function(grunt) {

	// Load the plugin that provides the "coffee" task.
	//inspect ngmin
	grunt.loadNpmTasks('grunt-contrib-coffee');
	grunt.loadNpmTasks('grunt-contrib-uglify');

	// Project configuration.
	grunt.initConfig({

		coffee: {
			target1: {
				expand: true,
				flatten: true,
				cwd: 'src/main/resources/toserve/assets/js/src/',
				src: ['*.coffee'],
				dest: 'src/main/resources/toserve/assets/js/build/',
				ext: '.js'
			},
			target2: {
				//http://stackoverflow.com/questions/10008941/remove-coffeescript-anonymous-function-calls
				options: {
      				bare: true
    			},				
				files: {
					'src/main/resources/toserve/assets/js/build/santix.js': 'src/main/resources/toserve/assets/js/src/*.coffee'
				}
			}
		},
  		uglify: {
    		target3: {
    			options: {
    				mangle: false
  				},
      			files: {
        			'src/main/resources/toserve/assets/js/santix.min.js': ['src/main/resources/toserve/assets/js/build/santix.js']
      			}
    		}
  		}

	});

	// Define the default task
	grunt.registerTask('default', ['coffee','uglify']);


};