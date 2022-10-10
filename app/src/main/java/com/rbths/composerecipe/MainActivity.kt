package com.rbths.composerecipe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.ContentAlpha.medium
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.Medium
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.google.accompanist.insets.LocalWindowInsets
import com.rbths.composerecipe.data.Recipe
import com.rbths.composerecipe.data.strawberryCake
import com.rbths.composerecipe.ui.theme.ComposeRecipeTheme
import com.rbths.composerecipe.ui.theme.Shapes
import com.google.accompanist.insets.statusBarsPadding
import kotlin.math.max
import kotlin.math.min


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }
    }
}

@Composable
fun MyApp(){
    ComposeRecipeTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.White
        ) {
            MainFragment(strawberryCake)
        }
    }
}

@Composable
fun MainFragment(recipe: Recipe) {
    val scrollState = rememberLazyListState()
    Box {
        Content(recipe, scrollState)
        ParallaxToolbar(recipe, scrollState)
    }
}

@Composable
fun ParallaxToolbar(recipe: Recipe, scrollState: LazyListState) {
    val imageHeight = AppBarExpendedHeight - AppBarCollapsedHeight

    val maxOffset = with(LocalDensity.current) { imageHeight.roundToPx() } - LocalWindowInsets.current.statusBars.layoutInsets.top
    val offset = min(scrollState.firstVisibleItemScrollOffset, maxOffset)
    val offsetProgress = max(0f, offset * 3f - 2f * maxOffset) / maxOffset
    TopAppBar(contentPadding = PaddingValues(), backgroundColor = Color.White,
        modifier = Modifier
            .height(AppBarExpendedHeight)
            .offset { IntOffset(x = 0, y = -offset) },
        elevation = if (offset == maxOffset) 4.dp else 0.dp
        ) {
        Column() {
            Box(
                Modifier
                    .height(imageHeight)
                    .graphicsLayer {
                        alpha = 1f - offsetProgress
                    }
            ) {
                Image(painter = painterResource(id = R.drawable.strawberry_pie_1), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colorStops = arrayOf(
                                    Pair(0.4f, Transparent),
                                    Pair(1.0f, White)
                                )
                            )
                        )) {
                    
                }
                Row(modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.Bottom) {
                    Text(
                        recipe.category,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .clip(Shapes.small)
                            .background(LightGray)
                            .padding(vertical = 6.dp, horizontal = 16.dp)
                    )
                }
            }
            Column(
                Modifier
                    .fillMaxWidth()
                    .height(AppBarCollapsedHeight), verticalArrangement = Arrangement.Center) {
                Text(recipe.title,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(horizontal = (16 + 28 * offsetProgress).dp)
                        .scale(1f - 0.25f * offsetProgress))
            }
        }
    }
    
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
        .fillMaxWidth()
        .statusBarsPadding()
        .height(
            AppBarCollapsedHeight
        )
        .padding(horizontal = 16.dp)) {
        CircularButton(R.drawable.ic_arrow_back)
        CircularButton(R.drawable.ic_favorite)

    }
}

@Composable
fun CircularButton(iconResource:Int, color:Color = Gray,elevation: ButtonElevation? = ButtonDefaults.elevation(), onClick: () -> Unit = {}) {
    Button(onClick = onClick,
    contentPadding = PaddingValues(),
    shape = Shapes.small,
    colors = ButtonDefaults.buttonColors(backgroundColor = White, contentColor = color),
        elevation = elevation,
        modifier = Modifier
            .width(38.dp)
            .height(38.dp)
    ) {
        Icon(painter = painterResource(id = iconResource), contentDescription = null)
    }
}

@Composable
fun Content(recipe: Recipe, scrollState: LazyListState) {
    LazyColumn(contentPadding = PaddingValues(top = AppBarExpendedHeight), state = scrollState){
        item {
            BasicInfo(recipe)
            Description(recipe)
            ServingCalculator(recipe)
            IngredientsHeader()
            IngredientsList(recipe)
            ShoppingListButton()
            Reviews(recipe)
            Images(recipe)
        }
    }
}

@Composable
fun Images(recipe: Recipe) {
    Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween){
        Image(painter = painterResource(id = R.drawable.strawberry_pie_2), contentDescription = null,
        modifier = Modifier
            .weight(1f)
            .clip(Shapes.small))
        Spacer(modifier = Modifier.weight(0.1f))
        Image(painter = painterResource(id = R.drawable.strawberry_pie_3), contentDescription = null,
            modifier = Modifier
                .weight(1f)
                .clip(Shapes.small))
    }
}

@Composable
fun Reviews(recipe:Recipe) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Column {
            Text(text = "Reviews", fontWeight = Bold)
            Text(recipe.reviews, color = DarkGray)
        }
        Button(onClick = { /*TODO*/ }, elevation = null, colors = ButtonDefaults.buttonColors(
            backgroundColor = Transparent, contentColor = colorResource(id = R.color.pink)
        )) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "See All")
                Icon(painter = painterResource(id = R.drawable.ic_arrow_right), contentDescription = null)
            }
        }
    }
}

@Composable
fun ShoppingListButton() {
    Button(
        onClick = { /*TODO*/ },
        elevation = null,
        shape = Shapes.small,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = LightGray,
            contentColor = Color.Black
        ), modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = "Add to shopping list", modifier = Modifier.padding(8.dp))
    }
}

@Composable
fun IngredientsList(recipe: Recipe) {
    EasyGrid(nColumns = 3, items = recipe.ingredients) {
        IngredientCard(it.image, it.title,it.subtitle, Modifier)
    }
}

@Composable
fun <T>EasyGrid(nColumns:Int, items:List<T>, content: @Composable (T) -> Unit){
    Column(Modifier.padding(16.dp)) {
        for (i in items.indices step nColumns){
            Row{
                for (j in 0 until nColumns){
                    if(i+j < items.size){
                        Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.weight(1f)) {
                            content(items[i+j])
                        }
                    }else{
                        Spacer(Modifier.weight(1f, fill = true))
                    }
                }
            }
        }
    }
}
@Composable
fun IngredientCard(
    @DrawableRes iconResource: Int,
    title: String,
    subtitle: String,
    modifier: Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(bottom = 16.dp)
    ) {
        Card(
            shape = Shapes.large,
            elevation = 0.dp,
            backgroundColor = LightGray,
            modifier = Modifier
                .width(100.dp)
                .height(100.dp)
                .padding(bottom = 8.dp)
        ) {
            Image(
                painter = painterResource(id = iconResource),
                contentDescription = null,
                modifier = Modifier.padding(16.dp)
            )
        }
        Text(text = title, modifier = Modifier.width(100.dp), fontSize = 14.sp, fontWeight = Medium)
        Text(text = subtitle, color = DarkGray, modifier = Modifier.width(100.dp), fontSize = 14.sp)
    }
}


@Composable
fun IngredientsHeader() {
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .clip(Shapes.medium)
            .background(
                LightGray
            )
            .fillMaxWidth()
            .height(44.dp)) {
        TabButton("Ingredients", true, Modifier.weight(1f))
        TabButton("Tools", false, Modifier.weight(1f))
        TabButton("Steps", false, Modifier.weight(1f))

    }
}

@Composable
fun TabButton(text:String, active:Boolean,modifier: Modifier) {
    Button(onClick = { /*TODO*/ },
        shape = Shapes.medium,
        modifier = modifier.fillMaxHeight(),
        elevation = null,
        colors = if (active) ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.pink), contentColor = White) else ButtonDefaults.buttonColors(backgroundColor = Color.LightGray, contentColor = DarkGray)
    ) {
        Text(text)
    }
}

@Composable
fun ServingCalculator(recipe: Recipe) {
    var value by remember { mutableStateOf(6) }
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(Shapes.medium)
            .background(LightGray)
            .padding(horizontal = 16.dp)){
        Text(text = "Serving", Modifier.weight(1f), fontWeight = Medium)
        CircularButton(iconResource = R.drawable.ic_minus, elevation = null, color =  colorResource(id = R.color.pink)){ value-- }
        Text(text = "$value",Modifier.padding(16.dp), fontWeight = Medium)
        CircularButton(iconResource = R.drawable.ic_plus, elevation = null, color = colorResource(id = R.color.pink)){ value++ }
    }

}

@Composable
fun BasicInfo(recipe: Recipe) {
    Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier
        .fillMaxWidth()
        .padding(top = 16.dp)){
        InfoColumn(R.drawable.ic_clock, recipe.cookingTime)
        InfoColumn(R.drawable.ic_flame, recipe.energy)
        InfoColumn(R.drawable.ic_star, recipe.rating)

    }
}

@Composable
fun Description(recipe: Recipe){
    Text(text = recipe.description, fontWeight = Medium, modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp))
}

@Composable
fun InfoColumn(@DrawableRes iconResource: Int, text:String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally){
        Icon(painter = painterResource(id = iconResource), contentDescription = null, tint = colorResource(id = R.color.pink), modifier = Modifier.height(24.dp))
        Text(text = text, fontWeight = Bold)
    }
}

@Preview(showBackground = true, widthDp = 388, heightDp = 1400)
@Composable
fun DefaultPreview() {
    ComposeRecipeTheme {
        MainFragment(strawberryCake)
    }
}