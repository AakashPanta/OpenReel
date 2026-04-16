LazyRow(
    contentPadding = PaddingValues(horizontal = 16.dp),
    horizontalArrangement = Arrangement.spacedBy(12.dp)
) {
    items(trending) { item ->
        Card(
            modifier = Modifier.width(140.dp)
        ) {
            Column(Modifier.padding(12.dp)) {
                Text(item.label)
                Text(item.posts)
            }
        }
    }
}
