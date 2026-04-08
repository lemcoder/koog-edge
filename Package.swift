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
         url: "https://github.com/lemcoder/koog-edge/releases/download/0.0.4/KoogEdgeKit.xcframework.zip",
         checksum: "695ce686ce8b77c3433e92b416567236db9146817551b8a7a98e2021efb83d66")
   ]
)
