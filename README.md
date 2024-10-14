# APuZ Reader

APuZ Reader is an Android app designed to explore and read editions of the German magazine "Aus Politik und Zeitgeschichte" (APuZ).

## Technical Details

- Built for Android using Kotlin and Jetpack Compose
- Minimum SDK version: 24
- Target SDK version: 34
- The app uses the [APuZ RSS Feed](https://www.bpb.de/rss-feed/230868.rss) provided by the [bpb (Bundeszentrale für politische Bildung)](https://www.bpb.de)
- Upon first launch, informations need to be extracted from the respective website of each APuZ edition. Thus the first launch of the app may take some time to complete, but each subsequent launch just checks for a new edition.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

### Planned Features:

- Storing editions in a "Favorites" list for easier access
- Extending the library to more free content from the [bpb](https://www.bpb.de/).

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- The [Bundeszentrale für Politische Bildung](https://www.bpb.de/) for providing the content
