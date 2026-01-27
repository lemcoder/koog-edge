// swift-tools-version:5.3
import PackageDescription

let package = Package(
   name: "KoogEdgeKit",
   platforms: [
     .iOS(.v14),
   ],
   products: [
      .library(name: "KoogEdgeKit", targets: ["KoogEdgeKit"])
   ],
   targets: [
      .binaryTarget(
         name: "KoogEdgeKit",
         url: "https://github.com/lemcoder/koog-edge/releases/download/<VERSION>/KoogEdgeKit.xcframework.zip",
         checksum: "<CHECKSUM>")
   ]
)
