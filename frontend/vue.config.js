module.exports = {
  devServer: {
    port: 4007,
    proxy: {
      '/api': {
        target: 'http://localhost:8007',
        changeOrigin: true
      }
    }
  }
}
