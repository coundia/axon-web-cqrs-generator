import SwiftUI

func textField(_ label: String, text: Binding<String>) -> some View {
    TextField(label, text: text)
}

func doubleField(_ label: String, value: Binding<Double>) -> some View {
    TextField(label, value: value, format: .number)
}

func intField(_ label: String, value: Binding<Int>) -> some View {
    TextField(label, value: value, format: .number)
}

func dateField(_ label: String, date: Binding<Date>) -> some View {
    DatePicker(label, selection: date, displayedComponents: .date)
}
