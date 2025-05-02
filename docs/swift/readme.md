## Add the model in main 
 
```swift

import SwiftUI
import SwiftData

@main
struct MyMainApp: App {
    @Environment(\.scenePhase) private var scenePhase
    
    var sharedModelContainer: ModelContainer = {
        let schema = Schema([
            User.self,
            ----- to add ----
            Product.self
            ------ end --
        ])
        let modelConfiguration = ModelConfiguration(
            schema: schema,
            isStoredInMemoryOnly: false
        )
        do {
            return try ModelContainer(for: schema, configurations: [modelConfiguration])
        } catch {
            fatalError("Could not create ModelContainer: \(error)")
        }
    }()
```


# Add in navigation 

```swift
    var body: some View {
        NavigationView {
            VStack {
            ------to add ---
             NavigationLink(destination: ProductListView(modelContext: modelContext, user: user ?? anonUser)) {
                        HStack {
                            Image(systemName: "list.bullet")
                                .foregroundColor(.gray)
                            Text("Product")
                        }
                    }
			 ------- end ---
				etc ...
```