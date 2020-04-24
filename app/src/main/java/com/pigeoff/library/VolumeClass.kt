package com.pigeoff.library

class VolumeClass {
    var kind: String? = null
    var totalItems: Int? = null
    var items: ArrayList<Items>? = null

    class Items() {
        var kind: String? = null
        var id: String? = null
        var etag: String? = null
        var selfLink: String? = null
        var volumeInfo: VolumeInfo? = null
        var saleInfo: SaleInfo? = null


        class VolumeInfo() {
            var title: String? = null
            var subtitle: String? = null
            var authors: ArrayList<String>? = null
            var publisher: String? = null
            var publishedDate: String? = null
            var description: String? = null
            var industryIdentifiers: List<IndustryIdentifiers>? = null
            var readingModes: ReadingModes? = null
            var pageCount: Int? = null
            var printType: String? = null
            var categories: ArrayList<String>? = null
            var maturityRating: String? = null
            var allowAnonLogging: Boolean? = null
            var contentVersion: String? = null
            var panelizationSummary: PanelisationSummary? = null
            var imageLinks: ImageLinks? = null
            var language: String? = null
            var previewLink: String? = null
            var infoLink: String? = null
            var canonicalVolumeLink: String? = null
            var accessInfo: AccessInfo? = null

            class IndustryIdentifiers() {
                var type: String? = null
                var identifier: String? = null
            }

            class ReadingModes() {
                var text: Boolean? = null
                var image: Boolean? = null
            }

            class PanelisationSummary() {
                var containsEpubBubbles: Boolean? = null
                var containsImageBubbles: Boolean? = null
            }

            class ImageLinks() {
                var smallThumbnail: String? = null
                var thumbnail: String? = null
            }
        }

        class SaleInfo() {
            var country: String? = null
            var saleability: String? = null
            var isEbook: Boolean? = null
        }

        class AccessInfo() {
            var country: String? = null
            var viewability: String? = null
            var embeddable: Boolean? = null
            var publicDomain: Boolean? = null
            var textToSpeechPermission: Boolean? = null

        }


    }

}