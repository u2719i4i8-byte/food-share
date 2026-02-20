module.exports = {
    lintOnSave: false,
    assetsDir: 'static',
    parallel: false,
    publicPath: '/',
    devServer: {
        host: "localhost",
        port: 21092,
        https: false,
        proxy: {
            '/api': {
                target: 'http://localhost:21090',
                changeOrigin: true,
            },
        },
        overlay: {
            warning: false,
            errors: false
        },
    },
}
