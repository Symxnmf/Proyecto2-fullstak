module.exports = function(config) {
  config.set({
    basePath: '',
    frameworks: ['jasmine'],
    files: [
      'src/tests/**/*.spec.js'
    ],
    reporters: ['progress', 'kjhtml'],
    port: 9876,
    colors: true,
    browsers: ['Chrome'],
    singleRun: false,
    autoWatch: true,
    concurrency: Infinity
  });
};
