# APuZ Reader

APuZ Reader is an Android app designed to explore and read editions of the German magazine "Aus Politik und Zeitgeschichte" (APuZ). A signed APK is provided and can be found under [app/release](https://github.com/Lienhop/APuZ-Reader/tree/main/app/release).

<p align="middle"> 
  <img title="Overview Mode" src="https://github.com/user-attachments/assets/023db1d0-58c8-475f-b1b9-5bd39a2b4358" width=300" hspace=20 />
  <img title="Reading Mode" src="https://github.com/user-attachments/assets/fbf9a6cb-972c-452c-b09a-f2b9f8c604d4" width="300" /> 
</p>

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
- Adding a search functionality
- Extending the library to more free content from the [bpb](https://www.bpb.de/).

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- The [Bundeszentrale für Politische Bildung](https://www.bpb.de/) for providing the content
