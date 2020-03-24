package com.jmlizano.diditweethat

import com.jmlizano.diditweetthat.badWordsFilter
import org.scalatest.funsuite.{AnyFunSuite, AsyncFunSuite}

class TwitterAnalyzerSuite  extends AnyFunSuite {

  object testBadWordsFilter extends badWordsFilter

  test("badWordsFilter should return true for texts containing bad words") {
    val badText1 = "I hate my boss, she is horrible"
    val badText2 = "The earth is plain, you all are dumb"
    val badText3 = "I want to start a Zumba class but just Kpop dances"

    assert(testBadWordsFilter.containsBadWords(badText1))
    assert(testBadWordsFilter.containsBadWords(badText2))
    assert(testBadWordsFilter.containsBadWords(badText3))
  }

  test("badWordsFilter should return false for texts not containing bad words") {
    val goodText1 = "My boss is wonderful :)"
    val goodText2 = "If you think about it, you will see that the earth has to be round"

    assert(!testBadWordsFilter.containsBadWords(goodText1))
    assert(!testBadWordsFilter.containsBadWords(goodText2))
  }

}
