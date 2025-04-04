package uk.co.glamoor.stylists.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import uk.co.glamoor.stylists.model.Stylist;

import java.util.List;

@Repository
public interface StylistRepository extends MongoRepository<Stylist, String> {

    @Aggregation(pipeline = {
            // STAGE 1: Geo-filtering (uses 2dsphere index)
            """
            {
                $geoNear: {
                     near: { type: "Point", coordinates: [?0, ?1] },
                     distanceField: "distance",
                     spherical: true,
                     maxDistance: ?6
                     query: {
                         "serviceSpecifications": {
                              $elemMatch: {
                                  "service._id": { $in: ?2 }
                              }
                          },
                         status: "ACTIVE"
                     }
                 }
            }
            """,

            // STAGE 2: Check favorites (single lookup)
            """
            {
                $lookup: {
                    from: "stylist-favourites",
                    let: { stylistId: "$_id" },
                    pipeline: [
                        {
                            $match: {
                                $expr: {
                                    $and: [
                                        { $eq: ["$customer", ?3] },
                                        { $eq: ["$stylist", "$$stylistId"] }
                                    ]
                                }
                            }
                        }
                    ],
                    as: "isFavourite"
                }
            }
            """,

            // STAGE 3: Add liked flag
            """
            {
                $addFields: {
                    favourite: {
                        $gt: [{ $size: "$isFavourite" }, 0]
                    },
                    offersHomeService: {
                        $cond: {
                            if: { $gt: [{ $size: "$homeServiceSpecifications" }, 0] },
                            then: true,
                            else: false
                        }
                    }
                }
            }
            """,

            // STAGE 4: Sort (distance is already sorted by $geoNear, add rating)
            "{ $sort: { distance: 1, rating: -1 } }",

            // STAGE 5: Pagination
            "{ $skip: ?4 }",
            "{ $limit: ?5 }",

            // STAGE 6: Projection
            """
            {
                $project: {
                    id: 1,
                    banner: 1,
                    logo: 1,
                    rating: 1,
                    firstName: 1,
                    lastName: 1,
                    brand: 1,
                    alias: 1,
                    favourite: 1,
                    verified: 1,
                    location: 1,
                    locality: 1,
                    offersHomeService: 1,
                    serviceCategories: 1
                }
            }
            """
    })
    List<Stylist> findStylistsByPreferredServices(
            double longitude,
            double latitude,
            List<ObjectId> preferredServiceIds,
            String customerId,
            int skip,
            int limit,
            long maxDistance
    );

    @Aggregation(pipeline = {
            // STAGE 1: Geo-filtering with status check
            """
            {
                $geoNear: {
                    near: { type: "Point", coordinates: [?0, ?1] },
                    distanceField: "distance",
                    spherical: true,
                    maxDistance: ?5
                    query: {
                        status: "ACTIVE"
                    }
                }
            }
            """,

            """
            {
                $lookup: {
                    from: "stylist-favourites",
                    let: { stylistId: "$_id" },
                    pipeline: [
                        {
                            $match: {
                                $expr: {
                                    $and: [
                                        { $eq: ["$customer", ?4] },
                                        { $eq: ["$stylist", "$$stylistId"] }
                                    ]
                                }
                            }
                        }
                    ],
                    as: "isFavourite"
                }
            }
            """,

            // STAGE 2: Add home service flag
            """
            {
                $addFields: {
                    favourite: {
                        $gt: [{ $size: "$isFavourite" }, 0]
                    },
                    offersHomeService: {
                        $cond: {
                            if: { $gt: [{ $size: "$homeServiceSpecifications" }, 0] },
                            then: true,
                            else: false
                        }
                    }
                }
            }
            """,

            // STAGE 3: Sort by distance → popularity
            "{ $sort: { distance: 1, popularity: -1 } }",

            // STAGE 4: Pagination
            "{ $skip: ?2 }",
            "{ $limit: ?3 }",

            // STAGE 5: Projection
            """
            {
                $project: {
                    id: 1,
                    banner: 1,
                    logo: 1,
                    rating: 1,
                    firstName: 1,
                    lastName: 1,
                    brand: 1,
                    alias: 1,
                    favourite: 1,
                    verified: 1,
                    location: 1,
                    locality: 1,
                    offersHomeService: 1,
                    serviceCategories: 1
                }
            }
            """
    })
    List<Stylist> findFeaturedNearbyStylists(
            double longitude,
            double latitude,
            int skip,
            int limit,
            String customerId,
            long maxDistance
    );

    @Aggregation(pipeline = {
            // STAGE 1: Get recently viewed stylists
            """
            {
               $lookup: {
                   from: "recently-viewed-stylists",
                   let: { customerId: ?0 },
                   pipeline: [
                       { $match: { $expr: { $eq: ["$customer", "$$customerId"] } } },
                       { $sort: { time: -1 } },
                       { $limit: ?2 }
                   ],
                   as: "recentViews"
               }
           }
           """,

            // STAGE 2: Convert to array with null check
            """
                {
                     $addFields: {
                         viewedStylistIds: {
                            $map: {
                                      input: { $ifNull: ["$recentViews", []] },
                                      as: "view",
                                      in: { $toObjectId: "$$view.stylist" }
                                  }
                         }
                     }
                }
            """,

            // STAGE 3: Filter using $expr
            """
            {
                $match: {
                    status: "ACTIVE",
                    $expr: { $in: ["$_id", "$viewedStylistIds"] }
                }
            }
            """,

            // STAGE 4: Check if stylist is favorited
            """
            {
                $lookup: {
                    from: "stylist-favourites",
                    let: { stylistId: "$_id" },
                    pipeline: [
                        {
                            $match: {
                                $expr: {
                                    $and: [
                                        { $eq: ["$customer", ?0] },
                                        { $eq: ["$stylist", "$$stylistId"] }
                                    ]
                                }
                            }
                        }
                    ],
                    as: "favouriteData"
                }
            }
            """,

            // STAGE 5: Add computed fields
            """
            {
                $addFields: {
                    offersHomeService: { $gt: [{ $size: "$homeServiceSpecifications" }, 0] },
                    favourite: { $gt: [{ $size: "$favouriteData" }, 0] },
                    lastViewed: {
                        $arrayElemAt: [
                            {
                                $filter: {
                                    input: "$recentViews",
                                    as: "view",
                                    cond: { $eq: ["$$view.stylist", "$_id"] }
                                }
                            },
                            0
                        ]
                    }
                }
            }
            """,

            // STAGE 6: Sort by view time
            "{ $sort: { 'lastViewed.time': -1 } }",

            // STAGE 7: Pagination
            "{ $skip: ?1 }",
            "{ $limit: ?2 }",

            // STAGE 8: Final projection
            """
            {
                $project: {
                    id: 1,
                    banner: 1,
                    logo: 1,
                    rating: 1,
                    firstName: 1,
                    lastName: 1,
                    brand: 1,
                    alias: 1,
                    favourite: 1,
                    verified: 1,
                    location: 1,
                    locality: 1,
                    offersHomeService: 1,
                    serviceCategories: 1
                }
            }
            """
    })
    List<Stylist> findRecentlyViewedStylists(
            String customerId,
            int skip,
            int limit
    );

}
