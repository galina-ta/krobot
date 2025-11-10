@file:Suppress("SpellCheckingInspection")

package com.gala.krobot.engine.levels

import com.gala.krobot.engine.level.entity.Level
import com.gala.krobot.engine.level.entity.parseLevel
import kotlin.properties.PropertyDelegateProvider

val allLevels = mutableMapOf<String, Level>()

// @formatter:off

val demoLevel by level("""
22222
2s222
2  f2
22222
""".trim())

val dogLevel by level("""
2222222
22f22 2
2     2
22 22 2
22 22s2
2222222
""".trim())

val level1 by level("""
222222222
222   222
222 2 222
2   2   2
2 22222 2
2     2f2
222 2 222
222 2 222
22s 2  22
""".trim())

val level2 by level("""
2222222222
s        2
222222 222
2   2  222
2 2   2222
2  2222 f2
22       2
2222222222
""".trim())

val homework1DenisLevel by level("""
222222222
222   222
222 2 222
2   2   2
2 22222 2
2     2f2
222 2 222
222 2 222
22s 2  22
""".trim())

val homework1Variant1Level by level("""
222222222222222222
22222        22222
2222         22222
2    2222222  2222
2    2     2  2222
22s 2222222 f22222
""".trim())

val homework1Variant2Level by level("""
2222222222222222222
222     2222  22 f2
22  222  222  2  22
2  22222  22    222
2  22222        222
22  222  222  2  22
222 s2  2222  22  2
2222222222222222222
""".trim())

val homework1Variant3Level by level("""
22222222222222222222222222
2s2222222            22222
2   222   222222222    222
222  2  222222222222f2   2
2222   22222222222222222 2
222  2  22222222222222   2
2   222   2222222222   222
2 2222222            22222
22222222222222222222222222
""".trim())

val level3 by level("""
22222
2s *2
222f2
22222
""".trim())

val homework2Variant1Level by level("""
222222222222222222
222222      222222
2222  222222  2222
222 2222222222 222
222 2222222222 222
22 222222222222 22
22 222222222222 22
2 22222222222222 2
2 22s   22    22 2
2 2     22     2 2
22 2   * 22   2 22
222 2222  2222 222
2222 22   222 2222
2222 22*22222 2222
2222 22 22 22 2222
22222f       22222
222222222222222222
""".trim())

val level4 by level("""
2222222222
s        2
222222*222
2   2  222
2*2   2222
2  2222 f2
22   *   2
2222222222
""".trim())

val level5 by level("""
22222
2s #2
222f2
22222
""".trim())

val level6 by level("""
2222222222
s        2
222222#222
2   2  222
2#2   2222
2  2222 f2
22   #   2
2222222222
""".trim())

val homework3DenisLevel by level("""
     22222222222222222222
     2        2  2#       2
     2   2         22 2   2 22
222222       2    2  2222222  2
2    2            2           2
 22222     2   2 #   #     #   2
     2#22#   2   2       2   # 2
   222s   2       2  2222222#f2
  2   222222222222 22222222222
  2222  2222      2222  2222
""".trimIndent())

val homework3EduardLevel by level("""
2222
  s22
     2
      2
      2
       2
       222
 2###22   2
 2    2    2
2      2 2 2
2      2   2
2  2   2222
 2    #    2
  2222     2
     2  2222
     22#    2
    2        2
   2         2
  2           2
 2 2         2
 2 2#####2222
 2       2
  2    f2
""".trimIndent())

val level7 by level("""
22222222222
2f2222222s2
2  22222  2
22  222  22
222  2  222
2    2  c 2
2 2222222 2
2#2222222 2
2 2222222v2
2v #  c   2
22 22222 22
22222222222
""".trimIndent())

val homework4EduardLevel by level("""
 22222222222
2    f     2
222222     2         22222222222
     2     2        2           2
     2#####2    2222             2
     2     #2222                  222
     2     # 2                       22
     2     # 2                        22
      222222 v    c                   22
           2222 222                 22 2
              2 2 22222222222v222222   2
             2 2  2 2       2 2    2   2
             2 2   2 2      2 2  2 2   2
            2 2    2 2     2  2  2  2     
            2 2    2 2    2  c    2s2
           222      222   222     222
           222      222   222     222
""".trimIndent())

val homework4DenisLevel by level("""
             22222222222
   22222 2222           222222
 22   vf22  #                22222
22     v2 2  #                   22
 22    222   #                    22
  2       22 #         2    22222 2 2
  22    22#  #        2     v    2  22
    2  c   222 2     2    2222  c2  22
     222   2  2  2  2 22222 2 22 2
       2222    2  2  2    2 2  2s2
                22 22     22   22
               222222    222  222
""".trimIndent())

val level8 by level("""
222222222
222%%%222
222%2%222
2%%%2%%%2
2%22222%2
2%%%  2f2
222%2 222
222%2 222
22s%2  22
""".trim())

// @formatter:on

private fun level(draw: String) = PropertyDelegateProvider<Any?, Lazy<Level>> { _, property ->
    val level = parseLevel(draw)
    allLevels[property.name] = level
    lazyOf(level)
}
