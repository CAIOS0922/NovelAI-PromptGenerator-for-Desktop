// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.io.File
import java.lang.IllegalStateException

data class PromptData(
    val priority: Int,
    val spell: SpellData,
)

data class SpellData(
    val largeCategory: String,
    val middleCategory: String,
    val smallCategory: String,
    val command: String,
)

@Composable
fun App(spells: List<SpellData>) {
    val addedPrompts = remember { mutableStateListOf<PromptData>() }

    MaterialTheme {
        Column {
            PromptResultSection(addedPrompts)
            PromptTitleSection()

            LazyColumn(Modifier.weight(1f)) {
                items(
                    items = addedPrompts,
                    key = { it.toString() }
                ) { prompt ->
                    AddedPromptSection(
                        prompt = prompt,
                        onClickClear = { addedPrompts.remove(it) }
                    )
                }
            }

            AddTableSection(
                spells = spells,
                onRegisterPrompt = { prompt ->
                    when {
                        addedPrompts.contains(prompt) -> return@AddTableSection
                        addedPrompts.find { it.spell == prompt.spell } != null -> {
                            addedPrompts.remove(addedPrompts.find { it.spell == prompt.spell })
                            addedPrompts.add(prompt)
                        }
                        else -> addedPrompts.add(prompt)
                    }

                    val sortedData = addedPrompts.sortedByDescending { it.priority }

                    addedPrompts.clear()
                    addedPrompts.addAll(sortedData)
                }
            )
        }
    }
}

@Composable
private fun PromptResultSection(prompts: List<PromptData>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier
                .wrapContentWidth()
                .padding(horizontal = 8.dp),
            text = "呪文",
            style = TextStyle.Large.center().bold()
        )

        OutlinedTextField(
            modifier = Modifier.padding(start = 16.dp).weight(1f),
            value = prompts.distinctBy { it.spell.command.replace(Regex("[()]"), "") }.joinToString(separator = ",") { it.spell.command },
            onValueChange = {},
        )
    }
}

@Composable
private fun PromptTitleSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            text = "重み",
            style = TextStyle.Default.center()
        )

        Text(
            modifier = Modifier
                .weight(3f)
                .padding(horizontal = 8.dp),
            text = "大項目",
            style = TextStyle.Default.center()
        )

        Text(
            modifier = Modifier
                .weight(3f)
                .padding(horizontal = 8.dp),
            text = "中項目",
            style = TextStyle.Default.center()
        )

        Text(
            modifier = Modifier
                .weight(3f)
                .padding(horizontal = 8.dp),
            text = "小項目",
            style = TextStyle.Default.center()
        )

        Text(
            modifier = Modifier
                .weight(3f)
                .padding(horizontal = 8.dp),
            text = "コマンド",
            style = TextStyle.Default.center(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        Text(
            modifier = Modifier
                .weight(2f)
                .padding(horizontal = 8.dp),
            text = "クリア",
            style = TextStyle.Default.center(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun AddedPromptSection(
    prompt: PromptData,
    onClickClear: (PromptData) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            text = prompt.priority.toString(),
            style = TextStyle.Default.center()
        )

        Text(
            modifier = Modifier
                .weight(3f)
                .padding(horizontal = 8.dp),
            text = prompt.spell.largeCategory,
            style = TextStyle.Default.center()
        )

        Text(
            modifier = Modifier
                .weight(3f)
                .padding(horizontal = 8.dp),
            text = prompt.spell.middleCategory,
            style = TextStyle.Default.center()
        )

        Text(
            modifier = Modifier
                .weight(3f)
                .padding(horizontal = 8.dp),
            text = prompt.spell.smallCategory,
            style = TextStyle.Default.center()
        )

        Text(
            modifier = Modifier
                .weight(3f)
                .padding(horizontal = 8.dp),
            text = prompt.spell.command,
            style = TextStyle.Default.center(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        Button(
            modifier = Modifier
                .weight(2f)
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            onClick = { onClickClear.invoke(prompt) },
        ) {
            Text(
                text = "×",
                style = TextStyle.Default.bold().center().nightPrimaryColor()
            )
        }
    }
}

@Composable
private fun AddTableSection(
    spells: List<SpellData>,
    onRegisterPrompt: (PromptData) -> Unit,
) {
    var isVisiblePriorityMenu by remember { mutableStateOf(false) }
    var isVisibleLargeCategoryMenu by remember { mutableStateOf(false) }
    var isVisibleMiddleCategoryMenu by remember { mutableStateOf(false) }
    var isVisibleSmallCategoryMenu by remember { mutableStateOf(false) }

    var priority by remember { mutableStateOf(1) }
    var largeCategory by remember { mutableStateOf("選択してください") }
    var middleCategory by remember { mutableStateOf("選択してください") }
    var smallCategory by remember { mutableStateOf("選択してください") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.weight(1f)) {
            Column(
                modifier = Modifier
                    .clickable { isVisiblePriorityMenu = true }
                    .padding(16.dp),
            ) {
                Text(
                    modifier = Modifier
                        .width(256.dp),
                    text = "重み",
                    style = TextStyle.Small.center().secondaryColor()
                )

                Text(
                    modifier = Modifier
                        .width(256.dp)
                        .padding(top = 4.dp),
                    text = priority.toString(),
                    style = TextStyle.Default.center().primaryColor()
                )
            }

            DropdownMenu(
                modifier = Modifier
                    .width(256.dp)
                    .padding(horizontal = 8.dp),
                expanded = isVisiblePriorityMenu,
                onDismissRequest = { isVisiblePriorityMenu = false },
            ) {
                for (item in 1..10) {
                    DropdownMenuItem(onClick = {
                        priority = item
                        isVisiblePriorityMenu = false
                    }) {
                        Text(
                            text = item.toString(), style = TextStyle.Default.primaryColor().center()
                        )
                    }
                }
            }
        }

        Box(Modifier.weight(1f)) {
            Column(
                modifier = Modifier
                    .clickable { isVisibleLargeCategoryMenu = true }
                    .padding(16.dp),
            ) {
                Text(
                    modifier = Modifier
                        .width(256.dp)
                        .padding(top = 4.dp),
                    text = "大項目",
                    style = TextStyle.Small.center().secondaryColor()
                )

                Text(
                    modifier = Modifier
                        .width(256.dp),
                    text = largeCategory,
                    style = TextStyle.Default.center().primaryColor()
                )
            }

            DropdownMenu(
                modifier = Modifier
                    .width(256.dp)
                    .padding(horizontal = 8.dp),
                expanded = isVisibleLargeCategoryMenu,
                onDismissRequest = { isVisibleLargeCategoryMenu = false },
            ) {
                for (item in spells.map { it.largeCategory }.distinct()) {
                    DropdownMenuItem(onClick = {
                        largeCategory = item
                        isVisibleLargeCategoryMenu = false

                        if(spells.filter { it.largeCategory == largeCategory }.find { it.middleCategory == middleCategory } == null) {
                            middleCategory = "選択してください"
                        }

                        if(spells.filter { it.largeCategory == largeCategory || it.middleCategory == middleCategory }.find { it.smallCategory == smallCategory } == null) {
                            smallCategory = "選択してください"
                        }
                    }) {
                        Text(
                            text = item, style = TextStyle.Default.primaryColor().center()
                        )
                    }
                }
            }
        }

        Box(Modifier.weight(1f)) {
            Column(
                modifier = Modifier
                    .clickable { isVisibleMiddleCategoryMenu = true }
                    .padding(16.dp),
            ) {
                Text(
                    modifier = Modifier
                        .width(256.dp),
                    text = "中項目",
                    style = TextStyle.Small.center().secondaryColor()
                )

                Text(
                    modifier = Modifier
                        .width(256.dp)
                        .padding(top = 4.dp),
                    text = middleCategory,
                    style = TextStyle.Default.center().primaryColor()
                )
            }

            DropdownMenu(
                modifier = Modifier
                    .width(256.dp)
                    .padding(horizontal = 8.dp),
                expanded = isVisibleMiddleCategoryMenu,
                onDismissRequest = { isVisibleMiddleCategoryMenu = false },
            ) {
                for (item in spells.filter { it.largeCategory == largeCategory }.map { it.middleCategory }.distinct()) {
                    DropdownMenuItem(onClick = {
                        middleCategory = item
                        isVisibleMiddleCategoryMenu = false

                        if(spells.filter { it.largeCategory == largeCategory && it.middleCategory == middleCategory }.find { it.smallCategory == smallCategory } == null) {
                            smallCategory = "選択してください"
                        }
                    }) {
                        Text(
                            text = item, style = TextStyle.Default.primaryColor().center()
                        )
                    }
                }
            }
        }

        Box(Modifier.weight(1f)) {
            Column(
                modifier = Modifier
                    .clickable { isVisibleSmallCategoryMenu = true }
                    .padding(16.dp),
            ) {
                Text(
                    modifier = Modifier
                        .width(256.dp),
                    text = "小項目",
                    style = TextStyle.Small.center().secondaryColor()
                )

                Text(
                    modifier = Modifier
                        .width(256.dp)
                        .padding(top = 4.dp),
                    text = smallCategory,
                    style = TextStyle.Default.center().primaryColor()
                )
            }

            DropdownMenu(
                modifier = Modifier
                    .width(256.dp)
                    .padding(horizontal = 8.dp),
                expanded = isVisibleSmallCategoryMenu,
                onDismissRequest = { isVisibleSmallCategoryMenu = false },
            ) {
                for (item in spells.filter { it.largeCategory == largeCategory && it.middleCategory == middleCategory }.map { it.smallCategory }.distinct()) {
                    DropdownMenuItem(onClick = {
                        smallCategory = item
                        isVisibleSmallCategoryMenu = false
                    }) {
                        Text(
                            text = item, style = TextStyle.Default.primaryColor().center()
                        )
                    }
                }
            }
        }

        Button(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .weight(1f),
            onClick = {
                val spell = spells.find { it.largeCategory == largeCategory && it.middleCategory == middleCategory && it.smallCategory == smallCategory } ?: return@Button
                onRegisterPrompt(PromptData(priority, spell))
            }
        ) {
            Text(
                text = "登録",
                style = TextStyle.Default.bold().center().nightPrimaryColor()
            )
        }
    }
}

private fun readSpellData(fileName: String): List<SpellData> {
    val file = File("./$fileName")

    if(!file.exists() || !file.isFile) {
        throw IllegalStateException("Cannot read SpellFile.")
    }

    val text = file.readText()
    val lines = text.split("\r\n").toMutableList().apply { removeFirst() }
    val spells = mutableListOf<SpellData>()

    for (line in lines) {
        val items = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*\$)".toRegex())

        spells.add(
            SpellData(
                largeCategory = items[0].replace("\"", ""),
                middleCategory = items[1].replace("\"", ""),
                smallCategory = items[2].replace("\"", ""),
                command = items[3].replace("\"", "").replace("{", "(").replace("}", ")"),
            )
        )
    }

    return spells
}

fun main() = application {
    val spells = readSpellData("./spells.csv")

    Window(onCloseRequest = ::exitApplication) {
        App(spells)
    }
}
