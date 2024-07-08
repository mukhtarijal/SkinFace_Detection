package com.dicoding.capstone.dermaface.data.model

import com.google.gson.annotations.SerializedName

data class NewsResponse(

	@field:SerializedName("-O-gS8pWLl3rnbNg-rzz")
	val oGS8pWLl3rnbNgRzz: List<OGS8pWLl3rnbNgRzzItem?>? = null
)

data class OGS8pWLl3rnbNgRzzItem(

	@field:SerializedName("image_url")
	val imageUrl: String? = null,

	@field:SerializedName("link")
	val link: String? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("desc")
	val desc: String? = null
)
