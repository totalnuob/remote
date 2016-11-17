var webpack = require('webpack');
var HtmlWebpackPlugin = require('html-webpack-plugin');
var ExtractTextPlugin = require('extract-text-webpack-plugin');
var helpers = require('./helpers');

module.exports = {
    entry: {
        'polyfills': './src/polyfills.ts',
        'vendor': './src/vendor.ts',
        'app': './src/main.ts'
    },

    resolve: {
        extensions: ['', '.js', '.ts']
    },

    module: {
        loaders: [
            {
                test: /\.ts$/,
                loaders: ['ts', 'angular2-template-loader']
            },
            {
                test: /\.html$/,
                loader: 'html'
            },

            // *************** code below breaks glyphicons loading ***************************************
            {
                test: /\.(png|jpe?g|gif)$/,
                loader: 'file?name=assets/[name].[hash].[ext]'
            },
            //*********************************************************************************************

            //{
            //    test: /\.css$/,
            //    exclude: helpers.root('src', 'app'),
            //    loader: ExtractTextPlugin.extract('style', 'css?sourceMap')
            //},
            {
                test: /.css$/,
                exclude: helpers.root('src', 'app'),
                loaders:[ExtractTextPlugin.extract('style', 'css-loader'), 'to-string', 'css']
                //loaders:[ExtractTextPlugin.extract({fallbackLoader: 'style-loader', loader: 'css-loader'}), 'to-string', 'css']
            },
            {
                test: /\.css$/,
                include: helpers.root('src', 'app'),
                loader: 'raw'
            },
            {
                test: /\.scss$/,
                loaders: ['style', 'css', 'postcss', 'sass']
            },
            {
                test: /\.(woff2?|ttf|eot|svg)$/,
                loader: 'url?limit=10000'
            },

            //{ test: /bootstrap\/dist\/js\/umd\//, loader: 'imports?jQuery=jquery' }

            { test: require.resolve("jquery"), loader: "expose?$!expose?jQuery" } // TODO: temp fix for wysiwig
        ]
    },

    plugins: [
        new webpack.optimize.CommonsChunkPlugin({ // separate app, vendor and polyfills imports between files
            name: ['app', 'vendor', 'polyfills']
        }),

        new HtmlWebpackPlugin({ // insert js and css files into index.html
            template: 'src/index.html'
        }),
        new webpack.ProvidePlugin({
            $: 'jquery',
            jQuery: 'jquery',
            jquery: 'jquery'
        })
    ]
};
